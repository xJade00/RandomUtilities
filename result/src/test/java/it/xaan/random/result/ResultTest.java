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
package it.xaan.random.result;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ResultTest {

  // TODO: FINISH. AND RE-WRITE.
  private final Result<String> success = Result.of("Successful state.");
  private final Result<String> error = Result.error(new IllegalStateException("Error state."));
  private final Result<String> empty = Result.empty();

  @Test
  @Before
  public void testEmpty() {
    // These should be the exact same object. nullable == empty.
    // If this method succeeds then all tests that reference empty will work
    // on nullable too.
    final Result<String> nullable = Result.ofNullable(null);
    Assert.assertSame(nullable, empty);
  }

  @Test
  public void testOn() {
    final List<Result<String>> list = Arrays.asList(success, empty, error);
    final Consumer<String> successFailure =
        state -> Assert.fail("Got Success state when state was meant to be " + state);
    final Consumer<String> emptyFail =
        state -> Assert.fail("Got Empty state when state was meant to be " + state);
    final Consumer<String> errorFail =
        state -> Assert.fail("Got Error state when state was meant to be " + state);
    for (Result<String> result : list) {
      if (result.isSuccess()) {
        result
            .onSuccess(elem -> Assert.assertEquals("Successful state.", elem))
            .onEmpty(() -> emptyFail.accept("Success"))
            .onError(Object.class, $ -> errorFail.accept("Success"));
      } else if (result.isEmpty()) {
        result
            .onSuccess(elem -> successFailure.accept("Empty"))
            .onEmpty(
                () -> {
                  /* Do nothing on empty */
                })
            .onError(Object.class, $ -> errorFail.accept("Empty"));
      } else if (result.isError()) {
        result
            .onSuccess(elem -> successFailure.accept("Error"))
            .onEmpty(() -> emptyFail.accept("Error"))
            .onError(
                IllegalStateException.class,
                ex -> Assert.assertEquals("Error state.", ex.getMessage()))
            .onError(Throwable.class, ex -> Assert.assertEquals("Error state.", ex.getMessage()))
            .onError(
                NullPointerException.class,
                ex -> Assert.fail("NPE onError was called when the result is an ISE"));
      }
    }
  }
}
