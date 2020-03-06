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

import java.util.function.Consumer;
import javax.annotation.Nullable;

public class TemplateBuilder {
  private TemplateType type;
  private String key;
  private @Nullable Class<?> clazz;
  private @Nullable Consumer<Object> cons;
  private @Nullable Runnable runnable;

  private TemplateBuilder(final TemplateType type, final String key) {
    this.type = type;
    this.key = key;
  }

  public static TemplateBuilder of(final TemplateType type, final String key) {
    if (key.equals("") || Result.GLOBAL_TEMPLATING.exists(key))
      throw new IllegalArgumentException("Key [" + key + "] already exists.");
    return new TemplateBuilder(type, key);
  }

  public TemplateBuilder setType(TemplateType type) {
    this.type = type;
    return this;
  }

  public TemplateBuilder setKey(String key) {
    this.key = key;
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T> TemplateBuilder setConsumer(Class<? extends T> clazz, Consumer<T> cons) {
    this.clazz = clazz;
    this.cons = obj -> cons.accept((T) obj);
    return this;
  }

  public TemplateBuilder setRunnable(Runnable runnable) {
    this.runnable = runnable;
    return this;
  }

  <T> void build(TemplateScope scope, @Nullable Result<T> result) {
    if (type == TemplateType.EMPTY && runnable == null) {
      throw new IllegalStateException("Type can't be empty without calling setRunnable.");
    }
    if (type != TemplateType.EMPTY && (clazz == null || cons == null)) {
      throw new IllegalStateException("Type can't be " + type + " without calling setConsumer");
    }
    switch (scope) {
      case BOTH:
        throw new IllegalStateException("Scope can't be Both.");
      case GLOBAL:
        Result.GLOBAL_TEMPLATING.add(type, key, clazz, cons, runnable);
        break;
      case LOCAL:
        if (result != null) {
          result.getLocalTemplating().add(type, key, clazz, cons, runnable);
        }
    }
  }
}
