package org.feature.management.shared.record;

import org.feature.management.shared.utils.ETaggableEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a route that requires ETag validation.
 */
public record ETagRoute(
        Pattern pattern,
        Function<Matcher, UUID> idExtractor,
        Function<UUID, Mono<? extends ETaggableEntity>> entityFetcher
) {}
