package org.feature.management.exception;

import java.io.Serial;

public class AccessDeniedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public AccessDeniedException(String message) {
        super(message);
    }
}
