package com.github.hekmekk.result4j;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CheckedSupplierTest {

  @Test
  void get() throws Throwable {
    assertThat(((CheckedSupplier<String>) () -> "foo").get(), is("foo"));

    assertThrows(
        Exception.class,
        ((CheckedSupplier<String>)
                () -> {
                  throw new Exception();
                })
            ::get);
  }
}
