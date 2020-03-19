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

import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ResultTest {

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
  public void testIsSuccess() {
    Assert.assertTrue(success.isSuccess());
    Assert.assertFalse(empty.isSuccess());
    Assert.assertFalse(error.isSuccess());
  }

  @Test
  public void testIsEmpty() {
    Assert.assertFalse(success.isEmpty());
    Assert.assertTrue(empty.isEmpty());
    Assert.assertFalse(error.isEmpty());
  }

  @Test
  public void testIsError() {
    Assert.assertFalse(success.isError());
    Assert.assertFalse(empty.isError());

    // Test error overrides
    Assert.assertTrue(error.isError());
    Assert.assertTrue(error.isError(Throwable.class));
    Assert.assertTrue(error.isError(IllegalStateException.class));
    Assert.assertFalse(error.isError(NullPointerException.class));
  }

  @Test
  public void testOnSuccess() {
    success.onSuccess(elem -> Assert.assertEquals("Successful state.", elem));
    empty.onSuccess(elem -> Assert.fail("Supposedly empty object contains element " + elem));
    error.onSuccess(elem -> Assert.fail("Supposedly error object contains element " + elem));
  }

  @Test
  public void testOnEmpty() {
    success.onEmpty(() -> Assert.fail("Supposedly success object is empty"));
    Assert.assertThrows(NoSuchElementException.class, () -> empty.onEmpty(() -> {
      throw new NoSuchElementException("Empty properly thrown.");
    }));
    error.onEmpty(() -> Assert.fail("Supposedly error object is empty"));
  }

  @Test
  public void testOnError() {
    // In general
    success.onError(Throwable.class, err -> Assert
        .fail("Supposedly success object contains error with message: " + err.getMessage()));
    empty.onError(Throwable.class, err -> Assert
        .fail("Supposedly success object contains error with message: " + err.getMessage()));
    error.onError(Throwable.class, err -> Assert.assertEquals("Error state.", err.getMessage()));

    // Specific correct class
    success.onError(IllegalStateException.class, err -> Assert
        .fail("Supposedly success object contains error with message: " + err.getMessage()));
    empty.onError(IllegalStateException.class, err -> Assert
        .fail("Supposedly success object contains error with message: " + err.getMessage()));
    error.onError(IllegalStateException.class,
        err -> Assert.assertEquals("Error state.", err.getMessage()));

    // Specific incorrect class
    error.onError(NullPointerException.class, err -> Assert.fail("Got NPE"));
  }

  @Test
  public void testFilter() {
    // Filters should run on success only.
    Assert.assertSame(success, success.filter(str -> str.equals("Successful state.")));
    // Returnings false for filter should result in an empty Result.
    Assert.assertSame(empty, success.filter(str -> !str.equals("Successful state.")));

    // Calling on anything but success should have no change.
    Assert.assertSame(empty, empty.filter($ -> {
      Assert.fail("Filter predicate called on empty Result.");
      return false;
    }));
    Assert.assertSame(error, error.filter($ -> {
      Assert.fail("Filter predicate called on error Result.");
      return false;
    }));
  }

  @Test
  public void testMap() {
    // Maps should run on success only.
    success.map($ -> "Hello world").onSuccess(elem -> Assert.assertEquals("Hello world", elem));

    // Other states may not return the same exactly, but they should be the same type.
    Result<String> mappedEmpty = empty.map($ -> "Hello world");
    Assert.assertTrue(mappedEmpty.isEmpty());

    Result<String> mappedError = error.map($ -> "Hello world");
    Assert.assertTrue(mappedError.isError());
    Assert.assertTrue(mappedError.isError(Throwable.class));
    Assert.assertTrue(mappedError.isError(IllegalStateException.class));
    Assert.assertFalse(mappedError.isError(NullPointerException.class));
  }

  @Test
  public void testFlatMap() {
    final Result<String> mapped = Result.of("Hello world");
    // Maps should run on success only.
    success.flatMap($ -> mapped).onSuccess(elem -> Assert.assertEquals("Hello world", elem));

    // Other states may not return the same exactly, but they should be the same type.
    Result<String> mappedEmpty = empty.flatMap($ -> mapped);
    Assert.assertTrue(mappedEmpty.isEmpty());

    Result<String> mappedError = error.flatMap($ -> mapped);
    Assert.assertTrue(mappedError.isError());
    Assert.assertTrue(mappedError.isError(Throwable.class));
    Assert.assertTrue(mappedError.isError(IllegalStateException.class));
    Assert.assertFalse(mappedError.isError(NullPointerException.class));
  }

  @Test
  public void testGet() {
    // Get should never throw for success
    Assert.assertEquals("Success State", success.get());

    // But it should on other states.
    Assert.assertThrows(NoSuchElementException.class, empty::get);
    Assert.assertThrows(NoSuchElementException.class, error::get);
  }

  @Test
  public void testOrElse() {
    final String other = "Hello world";
    // Shouldn't be else for success
    Assert.assertEquals("Success State", success.orElse(other));
    Assert.assertNotNull(success.orElse(null));

    // Should be else for everything else
    Assert.assertEquals(other, empty.orElse(other));
    Assert.assertEquals(other, error.orElse(other));
  }

  @Test
  public void testOrElseThrow() {
    final String other = "Hello world";
    // Shouldn't be else for success
    Assert.assertEquals("Success State",
        success.orElseThrow(() -> new IllegalStateException("Shouldn't be here.")));

    // Should be else for everything else
    Assert.assertThrows(NoSuchElementException.class,
        () -> empty.orElseThrow(() -> new NoSuchElementException("No such element.")));
    Assert.assertThrows(NoSuchElementException.class,
        () -> error.orElseThrow(() -> new NoSuchElementException("No such element.")));
  }
}
