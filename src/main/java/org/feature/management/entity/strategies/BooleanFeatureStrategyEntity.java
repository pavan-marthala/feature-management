package org.feature.management.entity.strategies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.feature.management.entity.FeatureStrategyEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BooleanFeatureStrategyEntity extends FeatureStrategyEntity {
    private boolean value;

}
