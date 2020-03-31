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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class MapMemoryCacheTest {

  private MapMemoryCache<String, Integer> create() {
    return new MapMemoryCache<>(HashMap::new);
  }


  @Test
  @Before
  public void testStore() {
    final MapMemoryCache<String, Integer> cache = create();
    Assert.assertEquals(Optional.empty(), cache.store("one", 1));
    Assert.assertEquals(Optional.of(1), cache.store("one", 2));
  }

  @Test
  public void testInvalidate() {
    final MapMemoryCache<String, Integer> cache = create();
    cache.store("one", 1);
    int size = cache.size();

    Assert.assertEquals(Optional.empty(), cache.invalidate("unknown key"));
    Assert.assertEquals(Optional.of(1), cache.invalidate("one"));
    Assert.assertNotEquals(size, cache.size());
  }

  @Test
  public void testEntries() {
    final MapMemoryCache<String, Integer> cache = create();
    cache.store("one", 1);
    final Set<Pair<String, Integer>> entries = new HashSet<>(
        Collections.singletonList(Pair.from("one", 1)));
    Assert.assertEquals(entries, cache.entries());
  }

  @Test
  public void testGetOptional() {
    final MapMemoryCache<String, Integer> cache = create();
    cache.store("one", 1);
    Assert.assertEquals(Optional.empty(), cache.getOptional("unknown key"));
    Assert.assertEquals(Optional.of(1), cache.getOptional("one"));
  }

  @Test
  public void testMap() {
    final MapMemoryCache<String, Integer> cache = create();
    cache.store("one", 1);
    final Cache<String, Integer> mapped = cache.map((key, value) -> Pair.from("two", value * 2));
    Assert.assertEquals(Integer.valueOf(2), mapped.get("two"));
    Assert.assertNull(mapped.get("one"));
  }
}
