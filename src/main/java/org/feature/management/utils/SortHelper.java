package org.feature.management.utils;

import org.springframework.data.domain.Sort;

import java.util.Optional;

public class SortHelper {

    private SortHelper() {
    }

    public static Sort buildSort(String sortParam) {
        return Optional.ofNullable(sortParam)
                .filter(s -> !s.isBlank())
                .map(SortHelper::parseSort)
                .orElse(Sort.unsorted());
    }

    private static Sort parseSort(String sortParam) {
        String[] parts = sortParam.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = parts.length > 1 ? Sort.Direction.fromString(parts[1].trim()) : Sort.Direction.ASC;
        return Sort.by(direction, property);
    }
}
