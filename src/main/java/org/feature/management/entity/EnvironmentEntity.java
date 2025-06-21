package org.feature.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "environment")
@EntityListeners(AuditingEntityListener.class)
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnvironmentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Set<String> owners = new HashSet<>();

    @Version
    @Column(nullable = false)
    private Long etag;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant modifiedAt;
}
