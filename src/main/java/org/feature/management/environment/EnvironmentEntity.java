package org.feature.management.environment;

import lombok.*;
import org.feature.management.shared.utils.ETaggableEntity;
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

@Table("environment")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnvironmentEntity implements ETaggableEntity{

    @Id
    private UUID id;

    private String name;

    private String description;

    @Column("owners")
    private Set<String> owners = new HashSet<>();

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
