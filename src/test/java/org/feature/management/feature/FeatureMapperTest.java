package org.feature.management.feature;

import org.feature.management.models.Feature;
import org.feature.management.models.BooleanFeatureStrategy;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureMapperTest {

    private final FeatureMapper mapper = FeatureMapper.INSTANCE;

    @Test
    void shouldMapEntityToModel() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        BooleanFeatureStrategy config = new BooleanFeatureStrategy();
        FeatureEntity entity = FeatureEntity.builder()
                .id(id)
                .name("test-feature")
                .description("test description")
                .enabled(true)
                .owners(Set.of("admin"))
                .configuration(config)
                .etag(1L)
                .createdAt(now)
                .modifiedAt(now)
                .build();

        Feature model = mapper.toModel(entity);

        assertThat(model).isNotNull();
        assertThat(model.getId()).isEqualTo(id);
        assertThat(model.getName()).isEqualTo("test-feature");
        assertThat(model.getEnabled()).isTrue();
        assertThat(model.getOwners()).containsExactly("admin");
        assertThat(model.getConfiguration()).isNotNull();
    }

    @Test
    void shouldMapModelToEntity() {
        Feature model = new Feature();
        model.setName("feature-abc");
        model.setEnabled(false);
        model.setOwners(List.of("user1"));
        BooleanFeatureStrategy config = new BooleanFeatureStrategy();
        model.setConfiguration(config);

        FeatureEntity entity = mapper.toEntity(model);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("feature-abc");
        assertThat(entity.isEnabled()).isFalse();
        assertThat(entity.getOwners()).containsExactly("user1");
        assertThat(entity.getConfiguration()).isNotNull();
    }

    @Test
    void shouldMapNullEntityToNullModel() {
        assertThat(mapper.toModel(null)).isNull();
    }

    @Test
    void shouldMapNullModelToNullEntity() {
        assertThat(mapper.toEntity(null)).isNull();
    }
}
