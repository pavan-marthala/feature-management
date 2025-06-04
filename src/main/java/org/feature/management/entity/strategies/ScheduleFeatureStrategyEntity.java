package org.feature.management.entity.strategies;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.Entity;
import lombok.*;
import org.feature.management.entity.FeatureStrategyEntity;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "schedule_feature_strategy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("SCHEDULE_FEATURE_STRATEGY")
public class ScheduleFeatureStrategyEntity extends FeatureStrategyEntity {
    private String cron;
}
