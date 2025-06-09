package org.feature.management.exception;

import java.io.Serial;

public class EnvironmentException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public EnvironmentException(String message) {
        super(message);
    }
}
