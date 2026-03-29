package org.feature.management.shared.filters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.feature.management.feature.FeatureRepository;
import org.feature.management.shared.record.ETagRoute;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Pattern;

@Component
@Order(2)
@RequiredArgsConstructor
public class FeatureETagValidationFilter extends AbstractETagValidationFilter {

    private final FeatureRepository featureRepo;

    @PostConstruct
    public void init() {
        routes.add(new ETagRoute(
                Pattern.compile("^/features/([0-9a-fA-F\\-]+)$"),
                matcher -> UUID.fromString(matcher.group(1)),
                featureRepo::findById
        ));

        routes.add(new ETagRoute(
                Pattern.compile("^/features/([0-9a-fA-F\\-]+)/owners/[^/]+$"),
                matcher -> UUID.fromString(matcher.group(1)),
                featureRepo::findById
        ));
    }
}
