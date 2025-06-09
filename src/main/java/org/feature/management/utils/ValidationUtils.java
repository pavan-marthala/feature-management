package org.feature.management.utils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class ValidationUtils {

    public static void validateETagMatch(Long actualETag, Long ifMatchHeader, Supplier<? extends RuntimeException> exceptionSupplier) {
        Optional.ofNullable(actualETag)
                .filter(etag -> Objects.equals(etag, ifMatchHeader))
                .orElseThrow(exceptionSupplier);
    }
}
