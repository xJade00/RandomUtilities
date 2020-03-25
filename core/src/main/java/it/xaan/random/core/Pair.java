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

import java.util.Objects;
import javax.annotation.Nullable;

/**
 * Represents two values, for when you need to return multiple things.
 *
 * @param <A> The type of the first variable.
 * @param <B> The type for the second variable.
 * @since 1.0.0
 */
@SuppressWarnings("WeakerAccess")
public final class Pair<A, B> {

  @Nullable
  private final A first;
  @Nullable
  private final B second;

  // Constructor
  private Pair(@Nullable A first, @Nullable B second) {
    this.first = first;
    this.second = second;
  }

  /**
   * Constructs a new {@link Pair}.
   *
   * @param first  The possibly-null first element.
   * @param second The possibly-null second element.
   * @param <X>    The type of the first element.
   * @param <Y>    The type of the second element.
   * @return A new instance of Pair with the specified elements.
   * @since 1.0.0
   */
  public static <X, Y> Pair<X, Y> from(@Nullable X first, @Nullable Y second) {
    return new Pair<>(first, second);
  }

  /**
   * Getter for the first element.
   *
   * @return The possibly-null first element.
   * @since 1.0.0
   */
  public @Nullable
  A getFirst() {
    return first;
  }

  /**
   * Getter for the second element.
   *
   * @return The possibly-null second element.
   * @since 1.0.0
   */
  public @Nullable
  B getSecond() {
    return second;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Pair)) {
      return false;
    }
    Pair<?, ?> other = (Pair<?, ?>) obj;
    return Objects.equals(this.first, other.first) && Objects.equals(this.second, other.second);
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
