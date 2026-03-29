package org.feature.management.shared.utils;

import java.util.UUID;

public interface ETaggableEntity {
    UUID getId();
    Long getEtag();
}
