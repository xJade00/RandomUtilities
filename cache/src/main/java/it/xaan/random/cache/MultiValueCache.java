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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "WeakerAccess"})
public interface MultiValueCache<K, V, C extends Collection<V>> extends Cache<K, C> {

  // Return types are non-null by default but this tells IJ to override the superclass.

  /**
   * See {@link Cache#get(Object)}. Returns from this are guaranteed non-null unlike the
   * super-class.
   *
   * @param key The key to search for.
   * @return If the key doesn't exist in the map, the {@link Collection} will be empty.
   */
  @Nonnull
  C get(K key);

  /**
   * Getter for the {@link Supplier} of an empty collection for easy storing.
   *
   * @return A new {@link Collection} instance that can be stored or new keys.
   */
  Supplier<C> supplier();

  @Override
  default boolean has(K key) {
    return !get(key).isEmpty();
  }

  /**
   * Stores a number of values into the cache. See {@link Cache#store(Object, Object)}.
   *
   * @param key    The key.
   * @param values The values to store.
   * @return The last known elements of this key, if the key didn't exist this will return an {@link
   * Optional} containing an empty
   * {@link Collection}.
   */
  @SuppressWarnings("all")
  default Optional<C> store(K key, V... values) {
    if (!has(key)) {
      store(key, supplier().get());
    }
    C old = get(key);
    C stored = supplier().get();
    stored.addAll(old);
    for (V value : values) {
      // Null values not allowed
      if (value != null) {
        stored.add(value);
      }
    }
    store(key, stored);
    return Optional.of(old);
  }

  /**
   * Gets the key/value pairs of the cache, flattening to ensure exactly one value for every key.
   * <p>
   * For example, given a Set of: {@code Set(Pair[first=key,second=List(1, 2, 3)]}, flattening to
   * guarantee one value for every key results in {@code Set(Pair[first=key,second=1],
   * Pair[first=key,second=2], Pair[first=key,second=3])}
   *
   * @return A {@link Set} of all entries flattened.
   */
  @SuppressWarnings("ConstantConditions")
  default Collection<Pair<K, V>> flatEntries() {
    List<Pair<K, V>> flattened = new ArrayList<>();
    for (Pair<K, C> entry : entries()) {
      for (V value : entry.getSecond()) {
        flattened.add(Pair.from(entry.getFirst(), value));
      }
    }
    return flattened;
  }


}
