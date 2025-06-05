package org.feature.management.exception;

import java.io.Serial;

public class FeatureException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  public FeatureException(String message) {
    super(message);
  }
}
