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

import java.util.Objects;
import javax.annotation.Nullable;

public class Pair<A, B> {
  private final @Nullable A first;
  private final @Nullable B second;

  private Pair(@Nullable A first, @Nullable B second) {
    this.first = first;
    this.second = second;
  }

  public static <X, Y> Pair<X, Y> from(@Nullable X first, @Nullable Y second) {
    return new Pair<>(first, second);
  }

  public A getFirst() {
    return first;
  }

  public B getSecond() {
    return second;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Pair)) return false;
    Pair<?, ?> other = (Pair<?, ?>) obj;
    boolean secondCheck;
    if (this.second instanceof Class && other.second instanceof Class) {
      Class<?> ours = (Class<?>) this.second;
      Class<?> theirs = (Class<?>) other.second;
      secondCheck = theirs.isAssignableFrom(ours);
    } else {
      secondCheck = Objects.equals(this.second, other.second);
    }
    return Objects.equals(this.first, other.first) && secondCheck;
  }

  @Override
  public int hashCode() {
    return Objects.hash(first, second);
  }

  @Override
  public String toString() {
    return String.format("Pair[first=%s,second=%s]", first, second);
  }
}
