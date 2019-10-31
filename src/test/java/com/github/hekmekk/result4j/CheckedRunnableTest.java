package com.github.hekmekk.result4j;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CheckedRunnableTest {

  @Test
  void run() {
    assertDoesNotThrow(((CheckedRunnable) () -> {})::run);

    assertThrows(
        Exception.class,
        ((CheckedRunnable)
                () -> {
                  throw new Exception();
                })
            ::run);
  }
}
