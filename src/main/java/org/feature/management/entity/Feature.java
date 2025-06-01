package org.feature.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@ToString(exclude = {"owners", "strategies"})
@EqualsAndHashCode(exclude = {"owners", "strategies"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Feature {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;
    private boolean enabled;
    @ManyToOne
    @JoinColumn(name = "environment_id")
    private Environment environment;
    @ManyToMany(mappedBy = "features",cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Owner> owners;
    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeatureStrategy> strategies;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private Instant modifiedAt;
}
