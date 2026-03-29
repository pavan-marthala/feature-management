package org.feature.management.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.feature.management.environment.EnvironmentEntity;
import org.feature.management.feature.FeatureEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class R2dbcConfigTest {

    private R2dbcConfig r2dbcConfig;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        r2dbcConfig = new R2dbcConfig();
        objectMapper = new ObjectMapper();
    }

    @Test
    void r2dbcCustomConversions_ReturnsConversions() {
        R2dbcCustomConversions conversions = r2dbcConfig.r2dbcCustomConversions(objectMapper);
        assertThat(conversions).isNotNull();
    }

    @Test
    void environmentIdCallback_GeneratesIdIfNull() {
        BeforeConvertCallback<EnvironmentEntity> callback = r2dbcConfig.environmentIdCallback();
        EnvironmentEntity entity = new EnvironmentEntity();
        assertThat(entity.getId()).isNull();

        StepVerifier.create(callback.onBeforeConvert(entity, null))
                .consumeNextWith(e -> assertThat(e.getId()).isNotNull())
                .verifyComplete();
    }

    @Test
    void environmentIdCallback_DoesNotOverwriteExistingId() {
        BeforeConvertCallback<EnvironmentEntity> callback = r2dbcConfig.environmentIdCallback();
        java.util.UUID existingId = java.util.UUID.randomUUID();
        EnvironmentEntity entity = new EnvironmentEntity();
        entity.setId(existingId);

        StepVerifier.create(callback.onBeforeConvert(entity, null))
                .consumeNextWith(e -> assertThat(e.getId()).isEqualTo(existingId))
                .verifyComplete();
    }

    @Test
    void featureIdCallback_GeneratesIdIfNull() {
        BeforeConvertCallback<FeatureEntity> callback = r2dbcConfig.featureIdCallback();
        FeatureEntity entity = new FeatureEntity();
        assertThat(entity.getId()).isNull();

        StepVerifier.create(callback.onBeforeConvert(entity, null))
                .consumeNextWith(e -> assertThat(e.getId()).isNotNull())
                .verifyComplete();
    }

    @Test
    void featureIdCallback_DoesNotOverwriteExistingId() {
        BeforeConvertCallback<FeatureEntity> callback = r2dbcConfig.featureIdCallback();
        java.util.UUID existingId = java.util.UUID.randomUUID();
        FeatureEntity entity = new FeatureEntity();
        entity.setId(existingId);

        StepVerifier.create(callback.onBeforeConvert(entity, null))
                .consumeNextWith(e -> assertThat(e.getId()).isEqualTo(existingId))
                .verifyComplete();
    }
}
