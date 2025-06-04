package org.feature.management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.*;
import org.feature.management.entity.strategies.BooleanFeatureStrategyEntity;
import org.feature.management.entity.strategies.HTTPRequestFeatureStrategyEntity;
import org.feature.management.entity.strategies.JWTClaimFeatureStrategyEntity;
import org.feature.management.entity.strategies.ScheduleFeatureStrategyEntity;
import org.feature.management.enums.StrategyType;

import java.util.UUID;

@Entity(name = "feature_strategy")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "strategy_type")
@Getter
@Setter
@ToString(exclude = "feature")
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanFeatureStrategyEntity.class, name = "BOOLEAN_FEATURE_STRATEGY"),
        @JsonSubTypes.Type(value = JWTClaimFeatureStrategyEntity.class, name = "JWT_CLAIM_FEATURE_STRATEGY"),
        @JsonSubTypes.Type(value = HTTPRequestFeatureStrategyEntity.class, name = "HTTP_REQUEST_FEATTURE_STRATEGY"),
        @JsonSubTypes.Type(value = ScheduleFeatureStrategyEntity.class, name = "SCHEDULE_FEATURE_STRATEGY")
})
public abstract  class FeatureStrategyEntity {
    @Id
    @GeneratedValue
    private UUID id;
//    @Enumerated(EnumType.STRING)
//    @Column(name = "strategy_type")
//    private StrategyType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    @JsonIgnore
    private FeatureEntity feature;
}
