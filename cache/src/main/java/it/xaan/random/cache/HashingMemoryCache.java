package it.xaan.random.cache;

import it.xaan.random.core.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HashingMemoryCache<K, V> implements Cache<K, V> {

  private final Map<K, V> underlying;

  /**
   * Creates a new {@link HashingMemoryCache} with an initial capacity.
   *
   * @param intiialCapacity The initial capacity of this cache.
   */
  public HashingMemoryCache(int intiialCapacity) {
    this.underlying = new HashMap<>(intiialCapacity);
  }

  /**
   * See {@link #HashingMemoryCache(int)}. Initial capacity is set to 16.
   */
  public HashingMemoryCache() {
    this(16);
  }

  @Override
  public Optional<V> getOptional(K key) {
    return Optional.ofNullable(underlying.get(key));
  }

  @Override
  public Optional<V> store(K key, V value) {
    return Optional.ofNullable(underlying.put(key, value));
  }

  @Override
  public CompletableFuture<Optional<V>> invalidate(K key) {
    return CompletableFuture.supplyAsync(() -> Optional.ofNullable(underlying.remove(key)));
  }

  @Override
  public CompletableFuture<List<V>> invalidateWhere(Predicate<K> filter) {
    return invalidateWhere((key, value) -> filter.test(key))
        .thenApply(list -> list.stream().map(Pair::getSecond).collect(Collectors.toList()));
  }

  @SuppressWarnings("all")
  @Override
  public CompletableFuture<List<Pair<K, V>>> invalidateWhere(BiPredicate<K, V> filter) {
    return CompletableFuture.supplyAsync(() ->
        entries()
            .stream()
            .filter(pair -> filter.test(pair.getFirst(), pair.getSecond()))
            .map(x -> Pair.from(x.getFirst(), underlying.remove(x.getSecond())))
            .filter(pair -> pair.getSecond() != null)
            .collect(Collectors.toList())
    );
  }

  @Override
  public Set<Pair<K, V>> entries() {
    return underlying.entrySet().stream().map(entry -> Pair.from(entry.getKey(), entry.getValue()))
        .collect(Collectors.toSet());
  }

  @SuppressWarnings("all")
  @Override
  public <A, B> Cache<A, B> map(BiFunction<K, V, Pair<A, B>> mapper) {
    final Cache<A, B> cache = new HashingMemoryCache<>(size());
    entries().stream().map(entry -> mapper.apply(entry.getFirst(), entry.getSecond()))
        .forEach(entry -> cache.store(entry.getFirst(), entry.getSecond()));
    return cache;
  }

  @Override
  public int size() {
    return underlying.size();
  }
}
