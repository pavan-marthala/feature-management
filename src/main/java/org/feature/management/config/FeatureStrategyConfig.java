package org.feature.management.config;

import lombok.Data;
import org.feature.management.models.FeatureStrategyResponseInner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "feature")
@Data
public class FeatureStrategyConfig {
    private List<FeatureStrategyResponseInner> strategies;
}