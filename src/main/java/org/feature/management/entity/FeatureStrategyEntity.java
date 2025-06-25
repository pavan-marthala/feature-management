package org.feature.management.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.feature.management.entity.strategies.BooleanFeatureStrategyEntity;
import org.feature.management.entity.strategies.HTTPRequestFeatureStrategyEntity;
import org.feature.management.entity.strategies.JWTClaimFeatureStrategyEntity;
import org.feature.management.entity.strategies.ScheduleFeatureStrategyEntity;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "strategy")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanFeatureStrategyEntity.class, name = "BooleanFeatureStrategy"),
        @JsonSubTypes.Type(value = JWTClaimFeatureStrategyEntity.class, name = "JWTClaimFeatureStrategy"),
        @JsonSubTypes.Type(value = HTTPRequestFeatureStrategyEntity.class, name = "HTTPRequestFeatureStrategy"),
        @JsonSubTypes.Type(value = ScheduleFeatureStrategyEntity.class, name = "ScheduleFeatureStrategy")
})
public interface FeatureStrategyEntity {

}
