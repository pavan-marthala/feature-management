package org.feature.management.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.feature.management.config.converters.FeatureConfigurationToJsonReadingConverter;
import org.feature.management.config.converters.FeatureConfigurationToJsonWritingConverter;
import org.feature.management.config.converters.SetToJsonReadingConverter;
import org.feature.management.config.converters.SetToJsonWritingConverter;
import org.feature.management.environment.EnvironmentEntity;
import org.feature.management.feature.FeatureEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableR2dbcAuditing
public class R2dbcConfig {

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(ObjectMapper objectMapper) {
        List<Object> converters = new ArrayList<>();
        converters.add(new SetToJsonWritingConverter(objectMapper));
        converters.add(new SetToJsonReadingConverter(objectMapper));
        converters.add(new FeatureConfigurationToJsonWritingConverter(objectMapper));
        converters.add(new FeatureConfigurationToJsonReadingConverter(objectMapper));
        return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, converters);
    }

    @Bean
    public BeforeConvertCallback<EnvironmentEntity> environmentIdCallback() {
        return (entity, table) -> {
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
            }
            return Mono.just(entity);
        };
    }

    @Bean
    public BeforeConvertCallback<FeatureEntity> featureIdCallback() {
        return (entity, table) -> {
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
            }
            return Mono.just(entity);
        };
    }
}
