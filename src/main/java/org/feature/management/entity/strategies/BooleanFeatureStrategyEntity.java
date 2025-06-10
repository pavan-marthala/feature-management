package org.feature.management.entity.strategies;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.Entity;
import lombok.*;
import org.feature.management.entity.FeatureStrategyEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BooleanFeatureStrategyEntity extends FeatureStrategyEntity {
    private boolean value;
}
