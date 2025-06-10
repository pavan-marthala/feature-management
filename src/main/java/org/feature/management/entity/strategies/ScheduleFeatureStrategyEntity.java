package org.feature.management.entity.strategies;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.Entity;
import lombok.*;
import org.feature.management.entity.FeatureStrategyEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleFeatureStrategyEntity extends FeatureStrategyEntity {
    private String cron;
}
