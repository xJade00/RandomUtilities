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

import it.xaan.random.core.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
class ResultTemplate {

  private final Map<String, Object> keys = new HashMap<>();
  private final Map<Pair<String, Class<?>>, Consumer<Object>> success = new HashMap<>();
  private final Map<String, Runnable> empty = new HashMap<>();
  private final Map<Pair<String, Class<?>>, Consumer<Object>> error = new HashMap<>();

  ResultTemplate() {}

  public boolean exists(String key) {
    return keys.containsKey(key); // Gonna be a lot faster than iterating through every keyset
  }

  private Optional<Consumer<Object>> grab(
      String key, Class<?> clazz, Map<Pair<String, Class<?>>, Consumer<Object>> map) {
    // Due to equals and hashcode being implemented I can safely just create
    // a new instance of Pair to check, it's gonna be faster and
    // probably more memory efficient too. The only time it's gon
    return Optional.ofNullable(success.get(Pair.from(key.toLowerCase(), clazz)));
  }

  private Optional<Consumer<Object>> find(
      String key, Class<?> clazz, Map<Pair<String, Class<?>>, Consumer<Object>> map) {
    Optional<Consumer<Object>> optional = grab(key, clazz, map);
    System.out.println("MAP: " + map);
    if (!optional.isPresent()) {
      System.out.println("Not present");
      optional =
          map.entrySet()
              .stream()
              .filter(
                  x ->
                      x.getKey().getSecond().isAssignableFrom(clazz)
                          && x.getKey().getFirst().equals(key))
              .map(Entry::getValue)
              .findFirst();
    }
    return optional;
  }

  Optional<Consumer<Object>> success(String key, Class<?> clazz) {
    return find(key, clazz, success);
  }

  Optional<Runnable> empty(String key) {
    return Optional.ofNullable(empty.get(key));
  }

  Optional<Consumer<Object>> error(String key, Class<?> clazz) {
    return find(key, clazz, error);
  }

  boolean add(
      TemplateType type,
      String key,
      @Nullable Class<?> clazz,
      @Nullable Consumer<Object> cons,
      @Nullable Runnable run) {
    if (keys.containsKey(key)) {
      return false;
    }

    switch (type) {
      case SUCCESS:
        Objects.requireNonNull(clazz, "Class must be non-null for Success.");
        Objects.requireNonNull(clazz, "Consumer must be non-null for Success.");
        success.put(Pair.from(key, clazz), cons);
        break;

      case EMPTY:
        Objects.requireNonNull(run, "Runnable must be non-null for Empty.");
        empty.put(key, run);
        break;

      case ERROR:
        Objects.requireNonNull(clazz, "Class must be non-null for Error.");
        Objects.requireNonNull(clazz, "Consumer must be non-null for Error.");
        error.put(Pair.from(key, clazz), cons);
        break;

      default:
        throw new IllegalStateException("Type must be one of [Success, Error Empty]");
    }
    keys.put(key, null);
    return true;
  }

  boolean remove(String type, String key) {
    key = key.toLowerCase();
    if (!keys.containsKey(key)) {
      return false;
    }
    final String check = key;
    switch (type.toLowerCase()) {
      case "success":
        Class<?> clazz =
            success
                .keySet()
                .stream()
                .filter(x -> x.getFirst().equals(check))
                .map(Pair::getSecond)
                .findAny()
                .orElse(null);
        success.remove(Pair.from(key, clazz));
        break;

      case "empty":
        empty.remove(key);
        break;

      case "error":
        Class<?> clazzE =
            error
                .keySet()
                .stream()
                .filter(x -> x.getFirst().equals(check))
                .map(Pair::getSecond)
                .findAny()
                .orElse(null);
        error.remove(Pair.from(key, clazzE));
        break;

      default:
        throw new IllegalStateException("Type must be one of [Success, Error Empty]");
    }
    keys.remove(key);
    return true;
  }
}
