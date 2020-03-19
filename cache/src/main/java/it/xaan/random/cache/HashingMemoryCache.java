package it.xaan.random.cache;

import it.xaan.random.core.Pair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

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
  public Optional<V> invalidate(K key) {
    return Optional.ofNullable(underlying.remove(key));
  }

  @Override
  public Set<Pair<K, V>> entries() {
    Set<Pair<K, V>> set = new HashSet<>();
    Set<Entry<K, V>> entries = underlying.entrySet();
    for (Entry<K, V> entry : entries) {
      set.add(Pair.from(entry.getKey(), entry.getValue()));
    }
    return set;
  }

  @Override
  public <A, B> Cache<A, B> map(BiFunction<K, V, Pair<A, B>> mapper) {
    final Cache<A, B> cache = new HashingMemoryCache<>(size());
    Set<Pair<K, V>> entries = entries();
    for (Pair<K, V> entry : entries) {
      Pair<A, B> mapped = mapper.apply(entry.getFirst(), entry.getSecond());
      cache.store(mapped);
    }
    return cache;
  }

  @Override
  public int size() {
    return underlying.size();
  }
}
