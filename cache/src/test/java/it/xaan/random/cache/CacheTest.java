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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CacheTest {

  private Cache<String, String> create() {
    Cache<String, String> test = new Cache<String, String>() {
      private final Map<String, String> map = new HashMap<>();

      @Override
      public Optional<String> getOptional(String key) {
        return Optional.ofNullable(map.get(key));
      }

      @Override
      public Optional<String> store(String key, String value) {
        return Optional.ofNullable(map.put(key, value));
      }

      @Override
      public Optional<String> invalidate(String key) {
        return Optional.ofNullable(map.remove(key));
      }

      @Override
      public Set<Pair<String, String>> entries() {
        Set<Pair<String, String>> set = new HashSet<>();
        Set<Entry<String, String>> entries = map.entrySet();
        for (Entry<String, String> entry : entries) {
          set.add(Pair.from(entry.getKey(), entry.getValue()));
        }
        return set;
      }
    };
    setup(test);
    return test;
  }


  private void setup(Cache<String, String> test) {
    test.store("key", "value");
    test.store("hello", "world");
  }

  @Test
  public void testMap() {
    final Cache<String, String> test = create();
    Assert.assertThrows(NotImplementedException.class, () -> test.map(($, $$) -> null));
  }

  @Test
  public void testStore() {
    final Cache<String, String> test = create();
    Assert.assertFalse(test.store(Pair.from("testStoreKey", "testStoreValue")).isPresent());
    Assert.assertEquals(Optional.of("testStoreValue"),
        test.store(Pair.from("testStoreKey", "otherTestStoreValue")));
  }

  @Test
  public void testInvalidateWhereKey() {
    final Cache<String, String> test = create();
    test.store("invalidateWhereK", "ignoredK");
    int before = test.size();
    List<String> invalidated = test.invalidateWhere(key -> key.equals("invalidateWhereK"));
    Assert.assertNotEquals(test.size(), before);
    Assert.assertFalse(invalidated.isEmpty());
    Assert.assertEquals("ignoredK", invalidated.get(0));
  }

  @Test
  public void testInvalidateWhereKeyValue() {
    final Cache<String, String> test = create();
    test.store("invalidateWhereKV", "ignoredKV");
    int before = test.size();
    List<Pair<String, String>> invalidated = test.invalidateWhere(
        (key, value) -> key.equals("invalidateWhereKV") && value.equals("ignoredKV"));
    Assert.assertNotEquals(test.size(), before);
    Assert.assertFalse(invalidated.isEmpty());
    Assert.assertEquals(Pair.from("invalidateWhereKV", "ignoredKV"), invalidated.get(0));
  }

  @Test
  public void testSize() {
    final Cache<String, String> test = create();
    Assert.assertEquals(2, test.size());
    test.store("new", "value");
    Assert.assertEquals(3, test.size());
  }

  @Test
  public void testGet() {
    final Cache<String, String> test = create();
    Assert.assertEquals("world", test.get("hello"));
    Assert.assertNull(test.get("unknown key"));
  }

  @Test
  public void testStream() {
    final Cache<String, String> test = create();
    List<Pair<String, String>> list = Arrays
        .asList(Pair.from("hello", "world"), Pair.from("key", "value"));
    test.stream().iterator().forEachRemaining(pair -> Assert.assertTrue(list.contains(pair)));
  }

  @Test
  public void testInvalidateAll() {
    final Cache<String, String> test = create();
    List<Pair<String, String>> list = test.invalidateAll();
    Assert.assertEquals(2, list.size());
    Assert.assertEquals(0, test.size());
    list.remove(Pair.from("hello", "world"));
    list.remove(Pair.from("key", "value"));
    Assert.assertTrue(list.isEmpty());
  }

  @Test
  public void testWhereKV() {
    final Cache<String, String> test = create();
    List<Pair<String, String>> empty = test.where(($, $$) -> false);
    Assert.assertTrue(empty.isEmpty());
    List<Pair<String, String>> found = test.where((key, $$) -> key.equals("key"));
    Assert.assertFalse(found.isEmpty());
    Assert.assertEquals(Pair.from("key", "value"), found.get(0));
  }

  @Test
  public void testWhereK() {
    final Cache<String, String> test = create();
    List<String> empty = test.where(key -> false);
    Assert.assertTrue(empty.isEmpty());
    List<String> found = test.where(key -> key.equals("key"));
    Assert.assertFalse(found.isEmpty());
    Assert.assertEquals("value", found.get(0));
  }

  @Test
  public void testHas() {
    final Cache<String, String> test = create();
    Assert.assertTrue(test.has("key"));
    Assert.assertFalse(test.has("unknown key"));
  }

  @Test
  public void testKeys() {
    final Cache<String, String> test = create();
    final Set<String> keys = new HashSet<>(Arrays.asList("key", "hello"));
    Assert.assertEquals(keys, test.keys());
  }

  @Test
  public void testValues() {
    final Cache<String, String> test = create();
    final List<String> control = Arrays.stream(new String[]{"value", "world"})
        .sorted()
        .collect(Collectors.toList());

    final List<String> values = test.values().stream()
        .sorted()
        .collect(Collectors.toList());

    Assert.assertEquals(control, values);
  }

}
