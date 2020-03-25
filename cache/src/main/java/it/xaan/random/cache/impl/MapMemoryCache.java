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
package it.xaan.random.cache.impl;

import it.xaan.random.cache.Cache;
import it.xaan.random.core.Pair;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

/**
 * Represents an in-memory cache that uses a {@link Map} as the underlying map.
 *
 * @param <K> The type of the keys.
 * @param <V> The type of the values.
 */
public class MapMemoryCache<K, V> implements Cache<K, V> {

  private final Map<K, V> underlying;

  /**
   * Creates a new {@link MapMemoryCache} with the specified {@link Map} as the underlying
   * implementation.
   *
   * @param underlying THe underlying Map to use.
   */
  public MapMemoryCache(final Map<K, V> underlying) {
    this.underlying = underlying;
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
  public int size() {
    return underlying.size();
  }

  @Override
  public int hashCode() {
    return underlying.hashCode();
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    return this == obj
        || obj instanceof MapMemoryCache<?, ?> && ((MapMemoryCache<K, V>) obj).underlying
        .equals(this.underlying);
  }

  @Override
  public String toString() {
    return String.format("MapMemoryCache[underlying=%s]", underlying);
  }
}
