package it.xaan.random.cache;

import it.xaan.random.core.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public interface Cache<K, V> {

  // Slightly modified from https://stackoverflow.com/a/35809896/10977609
  @SuppressWarnings("all")
  static <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> futuresList) {
    CompletableFuture<Void> allFuturesResult =
        CompletableFuture.allOf(futuresList.toArray((CompletableFuture<T>[]) new Object[0]));
    return allFuturesResult.thenApply(v ->
        futuresList.stream().
            map(CompletableFuture::join).
            collect(Collectors.<T>toList())
    );
  }

  /**
   * Gets a value from the cache.
   *
   * @param key The key of the value to grab.
   * @return An {@link Optional} that is empty if the provided key isn't in the cache. Otherwise an
   * Optional containing the value related the key.
   */
  Optional<V> getOptional(K key);

  /**
   * Stores the key and value inside the cache. Both should be non-null. If a null value is passed
   * the cache should fail to store, as if the method was never called.
   *
   * @param key   The key to save under.
   * @param value The value to save as.
   * @return The last known value of the key. If there was no value, it returns {@link Optional#empty()}
   */
  Optional<V> store(K key, V value);

  /**
   * Invalidates a specific key. This means that the key should be considered no longer safe to
   * use, even if it isn't immediately removed from the cache.
   *
   * @param key The key to invalidate.
   * @return A {@link CompletableFuture} with the last known value of the key. If there was no value,
   * the {@link Optional} will be empty. The future completes when the
   */
  CompletableFuture<Optional<V>> invalidate(K key);

  /**
   * Invalidates a number of entries based on the passed {@link Predicate}. This means that
   * none of the keys affected by this filter should be considered no longer safe to use,
   * even if none are immediately removed from the cache. Keys are invalidated when the
   * Predicate returns true.
   *
   * @param filter The Predicate for filtering.
   * @return A {@link CompletableFuture} that contains a {@link List} of values associated
   * with the keys that were invalidated.
   */
  CompletableFuture<List<V>> invalidateWhere(Predicate<K> filter);

  /**
   * Invalidates a number of entries based on the passed {@link BiPredicate}. This means that
   * none of the keys affected by this filter should be considered no longer safe to use,
   * even if none are immediately removed from the cache. Keys are invalidated when the
   * BiPredicate returns true.
   *
   * @param filter The BiPredicate for filtering.
   * @return A {@link CompletableFuture} that contains a {@link List} of key-value {@link Pair}s
   * that were invalidated.
   */
  CompletableFuture<List<Pair<K, V>>> invalidateWhere(BiPredicate<K, V> filter);

  /**
   * Gets the entries for this Cache. This is an immutable view and can't be interacted with.
   *
   * @return A {@link Set} containing all the entries of the cache.
   */
  Set<Pair<K, V>> entries();

  /**
   * Maps all current entries to new entries and returns them as the new {@link Cache}. It is on
   * the caller of the method to ensure there are no duplicates if they want the cache size to stay
   * the same. Any duplicates are found will be overwritten, with whatever the latest entry it
   * processes is.
   *
   * @param mapper The BiFunction that maps entries to new entries.
   * @param <A>    The key type of the new Cache.
   * @param <B>    The value type of the new Cache.
   * @return A new Cache containing the new entries the mapper found.
   */
  <A, B> Cache<A, B> map(BiFunction<K, V, Pair<A, B>> mapper);

  /**
   * Gets the size of the {@link Cache}.
   *
   * @return The current size of the cache.
   */
  default int size() {
    return entries().size();
  }

  /**
   * Gets the value associated with the key. See {@link #getOptional(Object)}.
   *
   * @param key The key to search for.
   * @return Possibly-null value associated with this key.
   */
  @Nullable
  default V get(K key) {
    return getOptional(key).orElse(null);
  }

  /**
   * Returns a {@link Stream} using the cache's entries from {@link #entries()} as the source.
   *
   * @return A new Stream containing all entries of this cache.
   */
  default Stream<Pair<K, V>> stream() {
    return entries().stream();
  }

  /**
   * Invalidates every entry in the cache. See {@link #invalidate(Object)}.
   *
   * @return A {@link CompletableFuture} that completes with all entries when every
   * entry has been invalidated.
   */
  default CompletableFuture<List<Pair<K, V>>> invalidateAll() {
    return invalidateWhere(($, $$) -> true);
  }

  /**
   * Returns all entries that match the provided {@link BiPredicate}.
   *
   * @param filter The BiPredicate that represents the filter.
   * @return A list of all entries in the {@link Cache} that match the filter.
   */
  default List<Pair<K, V>> where(BiPredicate<K, V> filter) {
    List<Pair<K, V>> list = new ArrayList<>();
    Set<Pair<K, V>> entries = entries();
    for (Pair<K, V> entry : entries) {
      if (filter.test(entry.getFirst(), entry.getSecond())) {
        list.add(entry);
      }
    }
    return list;
  }

  /**
   * Returns all values associated with keys that matched the passed {@link Predicate}.
   *
   * @param filter The Predicate that acts as a filter.
   * @return A list containing all values associated with matched keys.
   */
  default List<V> find(Predicate<K> filter) {
    List<V> list = new ArrayList<>();
    Set<Pair<K, V>> entries = entries();
    for (Pair<K, V> entry : entries) {
      if (filter.test(entry.getFirst())) {
        list.add(entry.getSecond());
      }
    }
    return list;
  }

  /**
   * Checks to see if the key exists inside the {@link Cache}.
   *
   * @param key The key to check.
   * @return {@code true} if the key exists, otherwise {@code false}.
   */
  default boolean has(K key) {
    return getOptional(key).isPresent();
  }

  /**
   * All keys from this {@link Cache}. This is copy and editing it will not affect the current
   * Cache.
   *
   * @return A set of all keys that are in this Cache.
   */
  default Set<K> keys() {
    Set<K> keys = new HashSet<>();
    entries().forEach(pair -> keys.add(pair.getFirst()));
    return keys;
  }

  /**
   * All values from this {@link Cache}. This is a copy and editing it will not affect the current
   * Cache.
   *
   * @return A set of all values that are in this cache.
   */
  default Set<V> values() {
    Set<V> values = new HashSet<>();
    entries().forEach(pair -> values.add(pair.getSecond()));
    return values;
  }
}
