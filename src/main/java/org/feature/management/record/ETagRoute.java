package org.feature.management.record;

import org.feature.management.interfaces.ETaggableEntity;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ETagRoute(
        Pattern pattern,
        Function<Matcher, UUID> idExtractor,
        Function<UUID, Optional<? extends ETaggableEntity>> entityFetcher
) {}
