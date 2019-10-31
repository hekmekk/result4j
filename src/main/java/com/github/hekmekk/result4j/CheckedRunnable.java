package com.github.hekmekk.result4j;

/** A {@linkplain Runnable} which may throw. */
@FunctionalInterface
public interface CheckedRunnable {

  /**
   * Performs side-effects.
   *
   * @throws Throwable if an error occurs
   */
  void run() throws Throwable;
}
