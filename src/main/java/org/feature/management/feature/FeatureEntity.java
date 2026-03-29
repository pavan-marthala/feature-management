package org.feature.management.feature;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.feature.management.shared.utils.ETaggableEntity;
import org.feature.management.models.FeatureConfiguration;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("feature")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureEntity implements ETaggableEntity{

    @Id
    private UUID id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    private boolean enabled;

    @Column("owners")
    private Set<String> owners = new HashSet<>();

    @Column("configuration")
    private FeatureConfiguration configuration;

    @Version
    @Column("etag")
    private Long etag;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("modified_at")
    private Instant modifiedAt;
}
