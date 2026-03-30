package org.feature.management.environment;

import org.feature.management.models.Environment;
import org.feature.management.models.EnvironmentRequest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentMapperTest {

    private final EnvironmentMapper mapper = EnvironmentMapper.INSTANCE;

    @Test
    void shouldMapEntityToModel() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        EnvironmentEntity entity = EnvironmentEntity.builder()
                .id(id)
                .name("test-env")
                .description("test description")
                .owners(Set.of("owner1", "owner2"))
                .etag(1L)
                .createdAt(now)
                .modifiedAt(now)
                .build();

        Environment model = mapper.toModel(entity);

        assertThat(model).isNotNull();
        assertThat(model.getId()).isEqualTo(id);
        assertThat(model.getName()).isEqualTo("test-env");
        assertThat(model.getDescription()).isEqualTo("test description");
        assertThat(model.getOwners()).containsExactlyInAnyOrder("owner1", "owner2");
        assertThat(model.getEtag()).isEqualTo(1L);
    }

    @Test
    void shouldMapRequestToEntity() {
        EnvironmentRequest request = new EnvironmentRequest();
        request.setName("env-name");
        request.setDescription("env-desc");
        request.setOwners(java.util.List.of("user1"));

        EnvironmentEntity entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("env-name");
        assertThat(entity.getDescription()).isEqualTo("env-desc");
        assertThat(entity.getOwners()).containsExactly("user1");
    }

    @Test
    void shouldMapModelToEntity() {
        EnvironmentRequest model = new EnvironmentRequest();
        model.setName("env-abc");
        model.setDescription("desc-abc");
        model.setOwners(List.of("user1"));

        EnvironmentEntity entity = mapper.toEntity(model);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("env-abc");
        assertThat(entity.getDescription()).isEqualTo("desc-abc");
        assertThat(entity.getOwners()).containsExactly("user1");
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
