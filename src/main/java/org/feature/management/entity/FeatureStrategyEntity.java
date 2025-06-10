package org.feature.management.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.feature.management.entity.strategies.BooleanFeatureStrategyEntity;
import org.feature.management.entity.strategies.HTTPRequestFeatureStrategyEntity;
import org.feature.management.entity.strategies.JWTClaimFeatureStrategyEntity;
import org.feature.management.entity.strategies.ScheduleFeatureStrategyEntity;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "strategy")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanFeatureStrategyEntity.class, name = "BOOLEAN_FEATURE_STRATEGY"),
        @JsonSubTypes.Type(value = JWTClaimFeatureStrategyEntity.class, name = "JWT_CLAIM_FEATURE_STRATEGY"),
        @JsonSubTypes.Type(value = HTTPRequestFeatureStrategyEntity.class, name = "HTTP_REQUEST_FEATURE_STRATEGY"),
        @JsonSubTypes.Type(value = ScheduleFeatureStrategyEntity.class, name = "SCHEDULE_FEATURE_STRATEGY")
})
public abstract class FeatureStrategyEntity {

}
