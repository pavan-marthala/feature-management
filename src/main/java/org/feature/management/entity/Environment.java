package org.feature.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Environment {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;

    @ManyToMany
    @JoinTable(
            name = "environment_owner",
            joinColumns = @JoinColumn(name = "environment_id"),
            inverseJoinColumns = @JoinColumn(name = "owner_id")
    )
    private Set<Owner> owners;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant modifiedAt;
}
