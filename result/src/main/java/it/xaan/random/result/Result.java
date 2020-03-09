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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

@SuppressWarnings({"unused", "unchecked", "WeakerAccess"})
public class Result<T> {

  private final T element;
  private final Object error;

  private static List<String> autoTemplates = new ArrayList<>();
  static final ResultTemplate GLOBAL_TEMPLATING = new ResultTemplate();
  private final ResultTemplate localTemplating = new ResultTemplate();

  private Result(@Nullable final T element, @Nullable final Object error) {
    if (element != null && error != null) {
      throw new IllegalStateException(
          "Both element and error can't be non-null. At least one must be null.");
    }
    this.element = element;
    this.error = error;
    applyAutos();
  }

  // Creation
  private static <U> Result<U> from(@Nullable U element, @Nullable Object error) {
    return new Result<>(element, error);
  }

  public static <U> Result<U> of(U element) {
    return from(element, null);
  }

  public static <U> Result<U> ofNullable(@Nullable U element) {
    return element == null ? empty() : of(element);
  }

  public static <U> Result<U> error(Object error) {
    return from(null, error);
  }

  public static <U> Result<U> empty() {
    return from(null, null);
  }

  // Templating
  public static void auto(String key) {
    if (GLOBAL_TEMPLATING.exists(key)) return;
    autoTemplates.add(key);
  }

  public static void addTemplate(TemplateBuilder builder) {
    builder.build(TemplateScope.GLOBAL, null);
  }

  public void addTemplate(TemplateScope scope, TemplateBuilder builder) {
    builder.build(scope, this);
  }

  public boolean exists(String key, TemplateScope scope) {
    switch (scope) {
      case BOTH:
        return exists(key, TemplateScope.GLOBAL) && exists(key, TemplateScope.LOCAL);
      case GLOBAL:
        return GLOBAL_TEMPLATING.exists(key);
      case LOCAL:
        return localTemplating.exists(key);
    }
    throw new IllegalArgumentException("Scope can't be null");
  }

  public void applyTemplates(TemplateScope scope, String... keys) {
    if (keys.length == 0) {
      return;
    }
    if (scope == TemplateScope.BOTH) {
      applyTemplates(TemplateScope.LOCAL, keys);
      applyTemplates(TemplateScope.GLOBAL, keys);
    }
    ResultTemplate template;
    switch (scope) {
      case LOCAL:
        template = getLocalTemplating();
        break;
      case GLOBAL:
        template = GLOBAL_TEMPLATING;
        break;
      default:
        throw new IllegalStateException(
            "If you see this then something is seriously wrong. Like seriously wrong. It physically should not be allowed here. It should stackoverflow before getting here.");
    }
    for (String key : keys) {
      if (!exists(key, TemplateScope.BOTH)) continue;
      if (isSuccess()) {
        template.success(key, element.getClass()).ifPresent(cons -> cons.accept(element));
      }
      if (isEmpty()) {
        template.empty(key).ifPresent(Runnable::run);
      }
      if (isError()) {
        template.error(key, error.getClass()).ifPresent(cons -> cons.accept(error));
      }
    }
  }

  // Checks
  public boolean isEmpty() {
    return this.element == null && this.error == null;
  }

  public boolean isSuccess() {
    return this.element != null;
  }

  public boolean isError() {
    return this.error != null;
  }

  public <U> boolean isError(Class<? extends U> clazz) {
    return isError() && clazz.isInstance(this.error);
  }

  // Methods for doing stuff, chaining
  public Result<T> onSuccess(Consumer<T> cons) {
    if (this.element != null) {
      cons.accept(this.element);
    }
    return this;
  }

  public Result<T> onEmpty(Runnable run) {
    if (isEmpty()) {
      run.run();
    }
    return this;
  }

  public <U> Result<T> onError(Class<? extends U> clazz, Consumer<U> cons) {
    if (clazz.isInstance(this.error)) {
      cons.accept((U) this.error);
    }
    return this;
  }

  // Functional methods for further control
  public <U> Result<U> filter(Predicate<T> filter) {
    if (isEmpty()) {
      return empty();
    }
    if (this.element != null && filter.test(this.element)) {
      return Result.empty();
    }
    return error(this.error);
  }

  public <U> Result<U> map(Function<? super T, U> func) {
    if (isEmpty()) {
      return empty();
    }
    if (this.element != null) {
      return ofNullable(func.apply(this.element));
    }
    return error(this.error);
  }

  public <U> Result<U> flatMap(Function<T, Result<U>> func) {
    if (isEmpty()) {
      return empty();
    }
    if (this.element != null) {
      return of(func.apply(this.element).element);
    }
    return error(this.error);
  }

  public T get() {
    if (!isSuccess()) {
      throw new NoSuchElementException("Get call on non-successful Result.");
    }
    return this.element;
  }

  public @Nullable T orElse(@Nullable T other) {
    if (!isSuccess()) {
      return other;
    } else {
      return this.element;
    }
  }

  public @Nullable T orElseGet(Supplier<T> supplier) {
    return orElse(supplier.get());
  }

  public T orElseThrow(Supplier<? extends Throwable> supplier) throws Throwable {
    if (isSuccess()) {
      return element;
    }
    throw supplier.get();
  }

  // Non public methods
  ResultTemplate getLocalTemplating() {
    return this.localTemplating;
  }

  private void checkAutos() {
    autoTemplates =
        autoTemplates.stream().filter(GLOBAL_TEMPLATING::exists).collect(Collectors.toList());
  }

  private void applyAutos() {
    checkAutos();
    autoTemplates.forEach(
        key -> {
          if (isEmpty()) {
            GLOBAL_TEMPLATING.empty(key).ifPresent(Runnable::run);
          }

          if (isSuccess()) {
            GLOBAL_TEMPLATING
                .success(key, this.element.getClass())
                .ifPresent(cons -> cons.accept(element));
          }

          if (isError()) {
            GLOBAL_TEMPLATING
                .error(key, this.error.getClass())
                .ifPresent(cons -> cons.accept(error));
          }
        });
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
