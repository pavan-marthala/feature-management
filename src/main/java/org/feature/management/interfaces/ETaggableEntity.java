package org.feature.management.interfaces;

import java.util.UUID;

public interface ETaggableEntity {
    UUID getId();
    Long getEtag();
}
