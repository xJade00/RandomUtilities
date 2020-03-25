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
package it.xaan.random.core;

import org.junit.Assert;
import org.junit.Test;

public class PairTest {

  private final Pair<String, String> test = Pair.from("Hello", "World");

  @Test
  public void testEquals() {
    final Pair<String, String> second = Pair.from("Hello", "World");
    final Pair<String, String> third = Pair.from("Hello", "World");
    // Test equals' 5 rules.
    // Reflexive.
    Assert.assertEquals(test, test);
    // Symmetric
    Assert.assertEquals(test, second);
    Assert.assertEquals(second, test);
    // Transitive
    Assert.assertEquals(test, second);
    Assert.assertEquals(second, third);
    Assert.assertEquals(test, third);
    // Consistent
    Assert.assertEquals(test, second);
    Assert.assertEquals(test, second);
    // null
    Assert.assertNotNull(test);
  }

  @Test
  public void testHashcode() {
    Pair<String, String> ea = Pair.from("Ea", "Ea");
    Pair<String, String> fb = Pair.from("FB", "FB");
    // Test hashcode's contract
    // Consistency
    Assert.assertEquals(72513, ea.hashCode());
    Assert.assertEquals(72513, ea.hashCode());
    // Same hashcode doesn't mean same object.
    Assert.assertEquals(ea.hashCode(), fb.hashCode());
    Assert.assertNotEquals(ea, fb);
  }

  @Test
  public void testGet() {
    Assert.assertEquals("Hello", test.getFirst());
    Assert.assertEquals("World", test.getSecond());
  }
}