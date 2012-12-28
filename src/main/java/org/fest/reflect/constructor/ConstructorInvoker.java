/*
 * Created on Oct 31, 2006
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 * Copyright @2006-2013 the original author or authors.
 */
package org.fest.reflect.constructor;

import static org.fest.reflect.util.Accessibles.makeAccessible;
import static org.fest.reflect.util.Accessibles.setAccessibleIgnoringExceptions;
import static org.fest.reflect.util.Throwables.targetOf;
import static org.fest.util.Preconditions.checkNotNull;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import javax.annotation.Nonnull;

import org.fest.reflect.exception.ReflectionError;

/**
 * Invokes a constructor via Java Reflection.
 *
 * <p>
 * Examples demonstrating usage of the fluent interface:
 *
 * <pre>
 *   // Equivalent to invoking 'new Person()'
 *   Person p = {@link org.fest.reflect.core.Reflection#constructor() constructor}().{@link TargetType#in in}(Person.class).{@link ConstructorInvoker#newInstance newInstance}();
 * 
 *   // Equivalent to invoking 'new Person("Yoda")'
 *   Person p = {@link org.fest.reflect.core.Reflection#constructor() constructor}().{@link TargetType#withParameterTypes(Class...) withParameterTypes}(String.class).{@link ParameterTypes#in(Class) in}(Person.class).{@link ConstructorInvoker#newInstance newInstance}("Yoda");
 * </pre>
 * </p>
 *
 * @param <T> the type in which the constructor is declared.
 * @author Alex Ruiz
 * @author Yvonne Wang
 */
public final class ConstructorInvoker<T> {
  private final Constructor<T> constructor;

  ConstructorInvoker(@Nonnull Class<T> target, @Nonnull Class<?>... parameterTypes) {
    checkNotNull(target);
    checkNotNull(parameterTypes);
    try {
      this.constructor = target.getDeclaredConstructor(parameterTypes);
    } catch (Throwable t) {
      String format = "Unable to find constructor in type %s with parameter types %s";
      // TODO: format array of Class.
      String msg = String.format(format, target.getName(), Arrays.toString(parameterTypes));
      throw new ReflectionError(msg);
    }
  }

  /**
   * Invokes the constructor of the specified type with the given arguments.
   * 
   * @param args the arguments to pass to the constructor (can be zero or more).
   * @return the created instance of <code>T</code>.
   * @throws ReflectionError if a new instance cannot be created.
   */
  public T newInstance(@Nonnull Object... args) {
    Constructor<T> c = checkNotNull(constructor);
    boolean accessible = constructor.isAccessible();
    try {
      makeAccessible(c);
      T newInstance = c.newInstance(args);
      return newInstance;
    } catch (Throwable t) {
      Throwable cause = targetOf(t);
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      throw new ReflectionError("Unable to create a new object from the enclosed constructor", cause);
    } finally {
      setAccessibleIgnoringExceptions(c, accessible);
    }
  }

  /**
   * @return the underlying constructor to invoke.
   */
  public @Nonnull Constructor<T> constructor() {
    return checkNotNull(constructor);
  }
}