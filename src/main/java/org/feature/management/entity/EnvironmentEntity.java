package org.feature.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
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
    @ElementCollection
    @CollectionTable(name = "environment_owners", joinColumns = @JoinColumn(name = "environment_id"))
    @Column(name = "owner")
    private Set<String> owners;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private Instant modifiedAt;
}
