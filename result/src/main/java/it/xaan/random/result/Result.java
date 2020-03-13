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
package it.xaan.random.result;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;

/**
 * Represents a Result. This can be thought of as a more expansive {@link java.util.Optional}. This
 * can be in one of three states: <br>
 * - Success <br>
 * - Error <br>
 * - Empty <br>
 * <br>
 * These states can be interacted with the methods {@link #onSuccess(Consumer)}, {@link
 * #onError(Class, Consumer)}, and {@link #onEmpty(Runnable)} respectively.
 *
 * @since 1.0.0
 */
@SuppressWarnings({"unused", "unchecked", "WeakerAccess", "null"})
public final class Result<T> {

  private final T element;
  private final Object error;

  private Result(@Nullable final T element, @Nullable final Object error) {
    this.element = element;
    this.error = error;
  }

  // Creation
  private static <U> Result<U> from(@Nullable U element, @Nullable Object error) {
    if (element != null && error != null) {
      // This should literally never happen. Unless the user is making some sort of reflection
      // hacks.
      throw new IllegalStateException(
          "Both element and error can't be non-null. At least one must be null. This shouldn't be possible. Please make an issue on the github.");
    }
    return new Result<>(element, error);
  }

  /**
   * Creates a new {@link Result} in a Success state with the specified non-null element. If your
   * element is possibly-null see {@link #ofNullable(Object)}
   *
   * @param element The element for this Result.
   * @param <U> The type of the result.
   * @return An instance of Result in the Success state.
   * @since 1.0.0
   */
  public static <U> Result<U> of(U element) {
    return from(Objects.requireNonNull(element), null);
  }

  /**
   * Creates a new {@link Result} in the Success state or the empty state.
   *
   * @param element The possibly-null element for this Result.
   * @param <U> The type of the result.
   * @return An instance of Result in the Success state if the element is non-null, otherwise it'll
   *     be in the Empty state.
   * @since 1.0.0
   */
  public static <U> Result<U> ofNullable(@Nullable U element) {
    return element == null ? empty() : of(element);
  }

  /**
   * Creates a new {@link Result} in the Error state.
   *
   * @param error The error for this Result.
   * @param <U> The type of the Result.
   * @return An instance of Result in the Error state.
   * @since 1.0.0
   */
  public static <U> Result<U> error(Object error) {
    return from(null, Objects.requireNonNull(error));
  }

  /**
   * Creates a new {@link Result} in the Empty state.
   *
   * @param <U> The type of the Result.
   * @return An instance of Result in the Empty state.
   * @since 1.0.0
   */
  public static <U> Result<U> empty() {
    return from(null, null);
  }

  // Checks

  /**
   * If the Result is of an Empty state.
   *
   * @return true if both {@link #isError()} and {@link #isSuccess()} return false.
   * @since 1.0.0
   */
  public boolean isEmpty() {
    return !isSuccess() && !isError();
  }

  /**
   * If the Result is of an Success state.
   *
   * @return true if both {@link #isError()} and {@link #isEmpty()} return false.
   * @since 1.0.0
   */
  public boolean isSuccess() {
    return this.element != null;
  }

  /**
   * If the Result is of an Error state.
   *
   * @return true if both {@link #isEmpty()} and {@link #isSuccess()} return false.
   * @since 1.0.0
   */
  public boolean isError() {
    return this.error != null;
  }

  /**
   * If the Result is of an Error state and the error is the specified {@link Class}
   *
   * @param clazz The class to check against.
   * @return true if it's this Result is in an Error state and the current error is an instance of
   *     the class.
   * @since 1.0.0
   */
  public boolean isError(Class<?> clazz) {
    return isError() && clazz.isInstance(this.error);
  }

  // Methods for doing stuff, chaining

  /**
   * Executes code when the {@link Result} is in the Success state defined by {@link #isSuccess()}
   * returning true. The {@link Consumer} takes in the current element, which is guaranteed to be
   * never null by definition.
   *
   * @param cons The consumer to run.
   * @return The current instance, useful for chaining.
   * @since 1.0.0
   */
  public Result<T> onSuccess(Consumer<T> cons) {
    if (isSuccess()) {
      cons.accept(this.element);
    }
    return this;
  }

  /**
   * Executes code when the {@link Result} is in the Empty state defined by {@link #isEmpty()}
   * returning true.
   *
   * @param run The runnable to run.
   * @return The current instance, useful for chaining.
   * @since 1.0.0
   */
  public Result<T> onEmpty(Runnable run) {
    if (isEmpty()) {
      run.run();
    }
    return this;
  }

  /**
   * Executes code when the {@link Result} is in the Error state defined by {@link #isError(Class)}
   * returning true. The {@link Consumer} takes in the current error, which is guaranteed to be
   * never null by definition. <br>
   * <br>
   * Please note that if the Result was made by giving a subclass, such as {@link
   * java.io.ByteArrayInputStream} then calling this method using {@code InputStream.class} as the
   * class parameter will work. This will always work when using {@code Object.class} as long as the
   * Result is in an error state. <br>
   * <br>
   * Also please note that due to type erasure there is no way for the code to implicitly know the
   * type parameters of the error, or even if it takes type parameters, without creating a lot of
   * extra work for everyone involved. The way around this is to do something similar to {@code
   * r.onError(List.class, (List<String> list) -> list.forEach(System.out::println));}. It's also
   * good to keep in mind that this will compile and run even if the type parameter is anything but
   * {@link String}. It'll error with a {@link ClassCastException} when you try and do anything that
   * is specific to String, rather than generic to {@link Object}. Please contact any library
   * developer that doesn't specify all possible errors so you don't have to guess.
   *
   * @param cons The consumer to run.
   * @param clazz The class to run on.
   * @return The current instance, useful for chaining.
   * @since 1.0.0
   */
  public <U> Result<T> onError(Class<? extends U> clazz, Consumer<U> cons) {
    if (isError(clazz)) {
      cons.accept((U) this.error);
    }
    return this;
  }

  // Functional methods for further control

  /**
   * Filters the current {@link Result}. The passed {@link Predicate} will only be called when the
   * Result is in a Success state. If the filter returns true then this will return the current
   * instance, otherwise it'll return empty. In any other state the current instance is returned.
   *
   * @param filter The predicate to test against.
   * @return An empty Result if the filter returns false and the current Result is in a Success
   *     state, otherwise the current instance.
   *     @since 1.0.0
   */
  public Result<T> filter(Predicate<T> filter) {
    return isSuccess() && !filter.test(element) ? empty() : this;
  }

  /**
   * Maps the current {@link Result} to a new Result. The passed {@link Function} will only be
   * called when the Result is in a Success state. If the function returns null this will return an
   * empty instance, otherwise it returns a new Result with the returned element. In any other state
   * a new Result is returned with the same information.
   *
   * @param func The function to map from the current element to a new one.
   * @param <U> The type of the new Result.
   * @return If the current Result is in the Success state, new Result in the Empty state if the
   *     function returns null, or a Success state with the new element. In any other state a new
   *     Result is returned with the same information.
   *     @since 1.0.0
   */
  public <U> Result<U> map(Function<? super T, U> func) {
    if (isEmpty()) {
      return empty();
    } else if (isSuccess()) {
      return ofNullable(func.apply(this.element));
    } else {
      // IntelliJ disagrees but it's not empty or a success, it's an error.
      return error(Objects.requireNonNull(this.error, "Should never happen. If this ever happens the world is ending."));
    }
  }

  /**
   * Flatmaps the current {@link Result} to a new Result. The passed {@link Function} will only be
   * called when the Result is in a Success state.
   *
   * @param func The function to map from the current element to a new one.
   * @param <U> The type of the new Result.
   * @return If the current Result is in the Success state, the Result returned from the function.
   *     In any other state a new Result is returned with the same information.
   * @since 1.0.0
   */
  public <U> Result<U> flatMap(Function<T, Result<U>> func) {
    if (isEmpty()) {
      return empty();
    } else if (isSuccess()) {
      return func.apply(this.element);
    } else {
      return error(Objects.requireNonNull(this.error, "Should never happen. If this ever happens the world is ending."));
    }
  }

  /**
   * Retrieves the current element from this {@link Result} if it's in a Success state, otherwise errors.
   *
   * @return The non-null element.
   * @throws NoSuchElementException If {@link #isSuccess()} returns false.
   * @since 1.0.0
   */
  public T get() {
    return orElseThrow(() -> new NoSuchElementException("Get call on non-successful Result."));
  }

  /**
   * Retrieves the current element from this {@link Result} if it's in a success state, otherwise
   * returns the passed parameter. It should be noted that while this is marked as nullable, it's
   * not possible for this to return null unless you pass {@code null} to the parameter.
   *
   * @param other The value to default to.
   * @return The current value, or the parameter passed if this Result isn't in a Success state.
   * @since 1.0.0
   */
  public @Nullable T orElse(@Nullable T other) {
    return isSuccess() ? get() : other;
  }

  /**
   * Retrieves the current element from this {@link Result} if it's in a success state, otherwise
   * returns the result of the passed {@link Supplier}. It should be noted that while this is marked as nullable, it's
   * not possible for this to return null unless you the supplier returns null.
   *
   * @param supplier The supplier to default to.
   * @return The current value, or the return value of the supplier passed if this Result isn't in a Success state.
   * @since 1.0.0
   */
  public @Nullable T orElseGet(Supplier<T> supplier) {
    return orElse(supplier.get());
  }

  /**
   * Retrieves the current element from this {@link Result} if it's in a success state, otherwise
   * throws the supplied exception.
   *
   * @param supplier The supplier of the exception.
   * @param <X> The element, useful for type checking.
   * @return The element.
   * @throws X If the Result is in a state other than Success.
   */
  public <X extends Throwable> T orElseThrow(Supplier<X> supplier) throws X {
    if (isSuccess()) {
      return element;
    }
    throw supplier.get();
  }

  // Overrides
  @Override
  public int hashCode() {
    return Objects.hash(this.element, this.error);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Result)) {
      return false;
    }
    Result<?> other = (Result<?>) obj;
    return Objects.equals(this.element, other.element) && Objects.equals(this.error, other.error);
  }

  @Override
  public String toString() {
    return String.format("Result[element=%s,error=%s]", this.element, this.error);
  }
}
