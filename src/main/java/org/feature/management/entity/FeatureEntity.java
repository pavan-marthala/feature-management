package org.feature.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.feature.management.enums.StrategyType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity(name = "feature")
@EntityListeners(AuditingEntityListener.class)
@Data
@ToString(exclude = { "strategy"})
@EqualsAndHashCode(exclude = { "strategy"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(name = "strategy_type")
    private StrategyType type;
    private boolean enabled;
    @ManyToOne
    @JoinColumn(name = "environment_id")
    private EnvironmentEntity environment;
    @ElementCollection
    @CollectionTable(name = "feature_owners", joinColumns = @JoinColumn(name = "feature_id"))
    @Column(name = "owner")
    private Set<String> owners;
    @OneToOne(mappedBy = "feature", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "strategy_id")
    @JsonProperty("strategy")
    private FeatureStrategyEntity strategy;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private Instant modifiedAt;
}
