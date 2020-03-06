/*
 * RandomUtilities - Random utilities I feel are nice.
 * Copyright © 2020 xaanit (shadowjacob1@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
    Assert.assertEquals(Pair.from("Hello", "World"), test);
  }

  @Test
  public void testHashcode() {
    Assert.assertEquals(-2053301055, test.hashCode());
  }

  @Test
  public void testGet() {
    Assert.assertEquals("Hello", test.getFirst());
    Assert.assertEquals("World", test.getSecond());
  }
}
