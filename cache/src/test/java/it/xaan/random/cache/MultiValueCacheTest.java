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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.junit.Assert;
import org.junit.Test;

public final class MultiValueCacheTest {

  private MultiValueCache<String, String, List<String>> create() {
    MultiValueCache<String, String, List<String>> test = new MultiValueCache<String, String, List<String>>() {
      private final Map<String, List<String>> map = new HashMap<>();
      @Override
      public Optional<List<String>> getOptional(String key) {
        return Optional.of(get(key)) ;
      }

      @Override
      public Optional<List<String>> store(String key, List<String> value) {
        List<String> old = map.put(key, value);
        return old == null ? Optional.of(Collections.emptyList()) : Optional.of(old);
      }

      @Override
      public Optional<List<String>> invalidate(String key) {
        List<String> old = map.remove(key);
        return old == null ? Optional.of(Collections.emptyList()) : Optional.of(old);
      }

      @Override
      public Set<Pair<String, List<String>>> entries() {
        Set<Pair<String, List<String>>> entries = new HashSet<>();
        for(Entry<String, List<String>> entry : map.entrySet()) {
          entries.add(Pair.from(entry.getKey(), entry.getValue()));
        }
        return entries;
      }

      @Nonnull
      @Override
      public List<String> get(String key) {
        List<String> list = map.get(key);
        return list == null ? Collections.emptyList() : list;
      }

      @Override
      public Supplier<List<String>> supplier() {
        return ArrayList::new;
      }
    };
    setup(test);
    return test;
  }


  private void setup(MultiValueCache<String, String, List<String>> test) {
    test.store("key", "value");
    test.store("hello", "world");
    test.store("two", "1", "2");
    test.store("three", "1", "2", "3");
  }

  @Test
  public void testHas() {
    final MultiValueCache<String, String, List<String>> test = create();
    Assert.assertTrue(test.has("key"));
    Assert.assertFalse(test.has("unknown key"));
  }

  @Test
  public void testStore() {
    final MultiValueCache<String, String, List<String>> test = create();
    Optional<List<String>> old = test.store("testStore", "1", "2", "3", "4", "5");
    Assert.assertTrue(old.isPresent() && old.get().isEmpty());
    Optional<List<String>> after = test.store("testStore", "1", "2", "3", "4");
    Assert.assertEquals(Arrays.asList("1", "2", "3", "4", "5"), after.orElse(Collections.emptyList()));
  }

  @Test
  public void testFlatEntries() {
    final MultiValueCache<String, String, List<String>> test = create();
    Set<Pair<String, String>> flattened = new HashSet<>( Arrays.asList(Pair.from("two", "1"), Pair.from("two", "2")));
    Assert.assertEquals(flattened, test.flatEntries());
  }

  @Test
  public void alwaysFail() {
    Assert.fail("Should always fail.");
  }
}
