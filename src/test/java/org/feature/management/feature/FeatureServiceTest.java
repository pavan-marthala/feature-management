package org.feature.management.feature;

import org.feature.management.config.FeatureStrategyConfig;
import org.feature.management.shared.exception.ResourceNotFoundException;
import org.feature.management.models.Feature;
import org.feature.management.models.FeatureStrategyResponseInner;
import org.feature.management.models.IdType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeatureServiceTest {

    @Mock
    private FeatureRepository featureRepository;

    @Mock
    private FeatureStrategyConfig strategyConfig;

    private FeatureService featureService;

    @BeforeEach
    void setUp() {
        featureService = new FeatureService(featureRepository, strategyConfig);
    }

    @Test
    void shouldCreateFeature() {
        Feature model = new Feature();
        model.setName("feature-1");
        FeatureEntity entity = new FeatureEntity();
        UUID generatedId = UUID.randomUUID();
        entity.setId(generatedId);

        when(featureRepository.existsByName("feature-1")).thenReturn(Mono.just(false));
        when(featureRepository.save(any(FeatureEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(featureService.createFeature(model))
                .expectNext(generatedId)
                .verifyComplete();

        verify(featureRepository).save(any(FeatureEntity.class));
    }

    @Test
    void shouldGetFeatureById() {
        UUID id = UUID.randomUUID();
        FeatureEntity entity = new FeatureEntity();
        entity.setId(id);
        entity.setName("feature-1");

        when(featureRepository.findById(id)).thenReturn(Mono.just(entity));

        StepVerifier.create(featureService.getById(id.toString(), IdType.ID))
                .consumeNextWith(model -> {
                    assertThat(model.getName()).isEqualTo("feature-1");
                })
                .verifyComplete();
    }

    @Test
    void shouldGetFeatureByName() {
        String name = "feature-name";
        FeatureEntity entity = new FeatureEntity();
        entity.setName(name);

        when(featureRepository.getByName(name)).thenReturn(Mono.just(entity));

        StepVerifier.create(featureService.getById(name, IdType.NAME))
                .consumeNextWith(model -> {
                    assertThat(model.getName()).isEqualTo(name);
                })
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenFeatureNotFound() {
        UUID id = UUID.randomUUID();
        when(featureRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(featureService.getById(id.toString(), IdType.ID))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void shouldGetAllFeatures() {
        FeatureEntity entity = new FeatureEntity();
        entity.setName("feature-1");

        when(featureRepository.findBy(any(Pageable.class))).thenReturn(Flux.just(entity));
        when(featureRepository.count()).thenReturn(Mono.just(1L));

        StepVerifier.create(featureService.getAllFeatures(0, 10, null))
                .consumeNextWith(page -> {
                    assertThat(page.getTotalElements()).isEqualTo(1);
                    assertThat(page.getContent().get(0).getName()).isEqualTo("feature-1");
                })
                .verifyComplete();
    }

    @Test
    void shouldGetAllStrategies() {
        FeatureStrategyResponseInner strategy = new FeatureStrategyResponseInner();
        when(strategyConfig.getStrategies()).thenReturn(List.of(strategy));

        StepVerifier.create(featureService.getAllFeatureStrategies())
                .expectNext(strategy)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenFeatureAlreadyExistsOnCreate() {
        Feature model = new Feature();
        model.setName("existing-feature");

        when(featureRepository.existsByName("existing-feature")).thenReturn(Mono.just(true));

        StepVerifier.create(featureService.createFeature(model))
                .expectError(org.feature.management.shared.exception.FeatureException.class)
                .verify();
    }

    @Test
    void shouldThrowExceptionWhenFeatureByNameNotFound() {
        String name = "non-existent";
        when(featureRepository.getByName(name)).thenReturn(Mono.empty());

        StepVerifier.create(featureService.getById(name, IdType.NAME))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void shouldAssignOwnerToFeature() {
        UUID id = UUID.randomUUID();
        FeatureEntity entity = new FeatureEntity();
        java.util.Set<String> owners = new java.util.HashSet<>();
        entity.setOwners(owners);

        when(featureRepository.findById(id)).thenReturn(Mono.just(entity));
        when(featureRepository.save(entity)).thenReturn(Mono.just(entity));

        StepVerifier.create(featureService.assignOwnerToFeature(id, "new-owner"))
                .verifyComplete();

        assertThat(entity.getOwners()).contains("new-owner");
    }

    @Test
    void shouldRemoveOwnerFromFeature() {
        UUID id = UUID.randomUUID();
        java.util.Set<String> owners = new java.util.HashSet<>();
        owners.add("owner1");
        owners.add("owner2");
        FeatureEntity entity = new FeatureEntity();
        entity.setOwners(owners);

        when(featureRepository.findById(id)).thenReturn(Mono.just(entity));
        when(featureRepository.save(entity)).thenReturn(Mono.just(entity));

        StepVerifier.create(featureService.removeOwnerFromFeature(id, "owner1"))
                .verifyComplete();

        assertThat(entity.getOwners()).doesNotContain("owner1");
        assertThat(entity.getOwners()).contains("owner2");
    }

    @Test
    void shouldThrowAccessDeniedWhenRemovingNonOwnerFromFeature() {
        UUID id = UUID.randomUUID();
        java.util.Set<String> owners = new java.util.HashSet<>();
        owners.add("owner1");
        FeatureEntity entity = new FeatureEntity();
        entity.setOwners(owners);

        when(featureRepository.findById(id)).thenReturn(Mono.just(entity));

        StepVerifier.create(featureService.removeOwnerFromFeature(id, "non-owner"))
                .expectError(org.feature.management.shared.exception.AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldThrowEnvironmentExceptionWhenRemovingLastOwnerFromFeature() {
        UUID id = UUID.randomUUID();
        java.util.Set<String> owners = new java.util.HashSet<>();
        owners.add("owner1");
        FeatureEntity entity = new FeatureEntity();
        entity.setOwners(owners);

        when(featureRepository.findById(id)).thenReturn(Mono.just(entity));

        StepVerifier.create(featureService.removeOwnerFromFeature(id, "owner1"))
                .expectError(org.feature.management.shared.exception.EnvironmentException.class)
                .verify();
    }

    @Test
    void shouldGetFeatureByNameDirectly() {
        String name = "feature-1";
        FeatureEntity entity = new FeatureEntity();
        entity.setName(name);

        when(featureRepository.getByName(name)).thenReturn(Mono.just(entity));

        StepVerifier.create(featureService.getFeatureByName(name))
                .consumeNextWith(model -> {
                    assertThat(model.getName()).isEqualTo(name);
                })
                .verifyComplete();
    }

    @Test
    void shouldUpdateFeatureConfig() {
        UUID id = UUID.randomUUID();
        org.feature.management.models.FeatureConfiguration config = new org.feature.management.models.BooleanFeatureStrategy();
        FeatureEntity entity = new FeatureEntity();

        when(featureRepository.findById(id)).thenReturn(Mono.just(entity));
        when(featureRepository.save(entity)).thenReturn(Mono.just(entity));

        StepVerifier.create(featureService.updateFeature(id, config))
                .verifyComplete();

        assertThat(entity.getConfiguration()).isEqualTo(config);
    }

    @Test
    void shouldDeleteFeature() {
        UUID id = UUID.randomUUID();
        FeatureEntity entity = new FeatureEntity();

        when(featureRepository.findById(id)).thenReturn(Mono.just(entity));
        when(featureRepository.delete(entity)).thenReturn(Mono.empty());

        StepVerifier.create(featureService.deleteById(id))
                .verifyComplete();

        verify(featureRepository).delete(entity);
    }
}
