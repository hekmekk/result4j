package com.github.hekmekk.result4j;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class DoneTest {

  @Test
  void getInstance() {
    assertThat(Done.getInstance(), instanceOf(Done.class));
  }

  @Test
  void done() {
    assertThat(Done.done(), instanceOf(Done.class));
  }

  @Test
  void equalsAndHashCode() {
    EqualsVerifier.forClass(Done.class).verify();
  }

  @Test
  void testToString() {
    assertThat(Done.getInstance().toString(), is("Done"));
  }
}
