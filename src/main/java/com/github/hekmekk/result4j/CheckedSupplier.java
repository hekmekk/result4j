package com.github.hekmekk.result4j;

/** A {@linkplain java.util.function.Supplier} which may throw. */
@FunctionalInterface
public interface CheckedSupplier<T> {

  /**
   * Provides a value.
   *
   * @throws Throwable if an error occurs
   */
  T get() throws Throwable;
}
