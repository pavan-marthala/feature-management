package org.feature.management.entity.strategies;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.Entity;
import lombok.*;
import org.feature.management.entity.FeatureStrategyEntity;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "boolean_feature_strategy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("BOOLEAN_FEATURE_STRATEGY")
public class BooleanFeatureStrategyEntity extends FeatureStrategyEntity {
    private boolean value;
}
