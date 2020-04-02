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
import it.xaan.random.cache.MultiValueCache;
import it.xaan.random.core.Pair;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

/**
 * Represents a {@link MultiValueCache} backed by a {@link Cache}.
 *
 * @param <K> The type for the keys.
 * @param <V> The type for the values.
 * @param <C> The type of Collection to use.
 */
public class MultiValueCacheDelegate<K, V, C extends Collection<V>> implements
    MultiValueCache<K, V, C> {

  private final Cache<K, C> delegate;
  private final Supplier<C> supplier;

  /**
   * Makes a new {@link MultiValueCacheDelegate} with the supplied delegate and supplier.
   *
   * @param delegate The {@link Cache} that methods delegate to.
   * @param supplier The {@link Supplier} for an empty collection for easy storing.
   */
  public MultiValueCacheDelegate(final Cache<K, C> delegate, final Supplier<C> supplier) {
    this.delegate = delegate;
    this.supplier = supplier;
  }

  @Override
  public Optional<C> getOptional(K key) {
    return Optional.of(delegate.getOptional(key).orElseGet(supplier()));
  }

  @Override
  public Optional<C> store(K key, C value) {
    Optional<C> old = getOptional(key);
    delegate.store(key, value);
    return old;
  }

  @Override
  public Optional<C> invalidate(K key) {
    Optional<C> old = getOptional(key);
    delegate.invalidate(key);
    return old;
  }

  @Override
  public Set<Pair<K, C>> entries() {
    return delegate.entries();
  }

  @Override
  public <A, B> Cache<A, B> map(BiFunction<K, C, Pair<A, B>> mapper) {
    return delegate.map(mapper);
  }

  @Nonnull
  @Override
  public C get(K key) {
    return delegate.getOptional(key).orElseGet(supplier());
  }

  @Override
  public Supplier<C> supplier() {
    return this.supplier;
  }
}
