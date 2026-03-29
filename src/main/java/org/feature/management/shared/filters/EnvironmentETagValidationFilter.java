package org.feature.management.shared.filters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.feature.management.environment.EnvironmentRepository;
import org.feature.management.shared.record.ETagRoute;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Pattern;

@Component
@Order(1)
@RequiredArgsConstructor
public class EnvironmentETagValidationFilter extends AbstractETagValidationFilter {

    private final EnvironmentRepository environmentRepo;

    @PostConstruct
    public void init() {
        routes.add(new ETagRoute(
                Pattern.compile("^/environments/([0-9a-fA-F\\-]+)$"),
                matcher -> UUID.fromString(matcher.group(1)),
                environmentRepo::findById
        ));
        routes.add(new ETagRoute(
                Pattern.compile("^/environments/([0-9a-fA-F\\-]+)/owners/[^/]+$"),
                matcher -> UUID.fromString(matcher.group(1)),
                environmentRepo::findById
        ));
    }
}
