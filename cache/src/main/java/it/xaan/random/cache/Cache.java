/*
 * Random Utilities - A bunch of random utilities I figured might be helpful.
 * Copyright © 2020 Jacob Frazier (shadowjacob1@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package it.xaan.random.cache;

import it.xaan.random.core.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Represents a Cache of objects that can be stored anywhere.
 *
 * @param <K> The type for the keys.
 * @param <V> The type for the values.
 */
@SuppressWarnings("unused")
public interface Cache<K, V> {

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
   * @return The last known value of the key. If there was no value, it returns {@link Optional#empty()}.
   */
  @SuppressWarnings("UnusedReturnValue")
  Optional<V> store(K key, V value);

  /**
   * Invalidates a specific key. This means that the key should be considered no longer safe to
   * use, even if it isn't immediately removed from the cache.
   *
   * @param key The key to invalidate.
   * @return A {@link Optional} with the last known value of the key. If there was no value,
   * the Optional will be empty.
   */
  Optional<V> invalidate(K key);

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
   * @throws NotImplementedException When the subclass doesn't allow mapping.
   * @return A new Cache containing the new entries the mapper found.
   */
  default <A, B> Cache<A, B> map(BiFunction<K, V, Pair<A, B>> mapper) {
    throw new NotImplementedException();
  }

  /**
   * See {@link #store(Object, Object)}.
   *
   * @param pair The key-value pair to add.
   * @return The last known value of the key. If there was no value, it returns {@link Optional#empty()}
   */
  @SuppressWarnings("ConstantConditions")
  default Optional<V> store(Pair<K, V> pair) {
    return store(pair.getFirst(), pair.getSecond());
  }


  /**
   * Invalidates a number of entries based on the passed {@link Predicate}. This means that
   * none of the keys affected by this filter should be considered no longer safe to use,
   * even if none are immediately removed from the cache. Keys are invalidated when the
   * Predicate returns true.
   *
   * @param filter The Predicate for filtering.
   * @return A {@link List} of values associated with the keys that were invalidated.
   */
  default List<V> invalidateWhere(Predicate<K> filter) {
    List<V> list = new ArrayList<>();
    List<Pair<K,V>> found = invalidateWhere((key, $) -> filter.test(key));
    for (Pair<K, V> kvPair : found) {
      list.add(kvPair.getSecond());
    }
    return list;
  }

  /**
   * Invalidates a number of entries based on the passed {@link BiPredicate}. This means that
   * none of the keys affected by this filter should be considered no longer safe to use,
   * even if none are immediately removed from the cache. Keys are invalidated when the
   * BiPredicate returns true.
   *
   * @param filter The BiPredicate for filtering.
   * @return A {@link List} of key-value {@link Pair}s that were invalidated.
   */
  @SuppressWarnings("ConstantConditions")
  default List<Pair<K, V>> invalidateWhere(BiPredicate<K, V> filter) {
    List<Pair<K, V>> list = new ArrayList<>();
    for (Pair<K, V> entry : entries()) {
      if(filter.test(entry.getFirst(), entry.getSecond())) {
        list.add(entry);
        invalidate(entry.getFirst());
      }
    }
    return list;
  }

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
   * @return A {@link List}  with all entries.
   */
  default List<Pair<K, V>> invalidateAll() {
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
