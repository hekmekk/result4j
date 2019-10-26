package com.github.hekmekk.result4j;

import java.io.Serializable;

/**
 * To be used in conjunction with e.g. {@link Result} to signal a successfully completed operation
 * without a corresponding value.
 *
 * <p>Based on: https://doc.akka.io/api/akka/current/akka/Done.html
 */
public enum Done implements Serializable {
  Done;

  private static final long serialVersionUID = 1L;

  public static Done getInstance() {
    return Done;
  }

  public static Done done() {
    return Done;
  }
}
