package org.feature.management.environment;

import org.feature.management.shared.exception.AccessDeniedException;
import org.feature.management.shared.exception.EnvironmentException;
import org.feature.management.shared.exception.ResourceNotFoundException;
import org.feature.management.models.EnvironmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvironmentServiceTest {

    @Mock
    private EnvironmentRepository environmentRepository;

    private EnvironmentService environmentService;

    @BeforeEach
    void setUp() {
        environmentService = new EnvironmentService(environmentRepository);
    }

    @Test
    void shouldCreateEnvironment() {
        EnvironmentRequest request = new EnvironmentRequest();
        request.setName("env-1");
        EnvironmentEntity entity = new EnvironmentEntity();
        UUID generatedId = UUID.randomUUID();
        entity.setId(generatedId);

        when(environmentRepository.save(any(EnvironmentEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(environmentService.createEnvironment(request))
                .expectNext(generatedId)
                .verifyComplete();

        verify(environmentRepository).save(any(EnvironmentEntity.class));
    }

    @Test
    void shouldGetEnvironmentById() {
        UUID id = UUID.randomUUID();
        EnvironmentEntity entity = new EnvironmentEntity();
        entity.setId(id);
        entity.setName("env-1");

        when(environmentRepository.findById(id)).thenReturn(Mono.just(entity));

        StepVerifier.create(environmentService.getById(id))
                .consumeNextWith(model -> {
                    assertThat(model).isNotNull();
                    assertThat(model.getName()).isEqualTo("env-1");
                })
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenEnvironmentNotFound() {
        UUID id = UUID.randomUUID();
        when(environmentRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(environmentService.getById(id))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void shouldGetAllEnvironments() {
        EnvironmentEntity entity = new EnvironmentEntity();
        entity.setName("env-1");

        when(environmentRepository.findBy(any(Pageable.class))).thenReturn(Flux.just(entity));
        when(environmentRepository.count()).thenReturn(Mono.just(1L));

        StepVerifier.create(environmentService.getAllEnvironments(0, 10, null))
                .consumeNextWith(page -> {
                    assertThat(page.getTotalElements()).isEqualTo(1);
                    assertThat(page.getContent()).hasSize(1);
                    assertThat(page.getContent().get(0).getName()).isEqualTo("env-1");
                })
                .verifyComplete();
    }

    @Test
    void shouldAssignOwnerToEnvironment() {
        UUID id = UUID.randomUUID();
        EnvironmentEntity entity = EnvironmentEntity.builder()
                .owners(new HashSet<>())
                .build();

        when(environmentRepository.findById(id)).thenReturn(Mono.just(entity));
        when(environmentRepository.save(any(EnvironmentEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(environmentService.assignOwnerToEnvironment(id, "new-owner"))
                .verifyComplete();

        assertThat(entity.getOwners()).contains("new-owner");
    }

    @Test
    void shouldRemoveOwnerFromEnvironment() {
        UUID id = UUID.randomUUID();
        // Needs at least 2 owners as per service logic
        Set<String> owners = new HashSet<>();
        owners.add("owner1");
        owners.add("owner2");
        EnvironmentEntity entity = EnvironmentEntity.builder()
                .owners(owners)
                .build();

        when(environmentRepository.findById(id)).thenReturn(Mono.just(entity));
        when(environmentRepository.save(any(EnvironmentEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(environmentService.removeOwnerFromEnvironment(id, "owner1"))
                .verifyComplete();

        assertThat(entity.getOwners()).doesNotContain("owner1");
        assertThat(entity.getOwners()).contains("owner2");
    }

    @Test
    void shouldThrowAccessDeniedWhenRemovingNonOwner() {
        UUID id = UUID.randomUUID();
        Set<String> owners = new HashSet<>();
        owners.add("owner1");
        EnvironmentEntity entity = EnvironmentEntity.builder()
                .owners(owners)
                .build();

        when(environmentRepository.findById(id)).thenReturn(Mono.just(entity));

        StepVerifier.create(environmentService.removeOwnerFromEnvironment(id, "non-owner"))
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldThrowEnvironmentExceptionWhenRemovingLastOwner() {
        UUID id = UUID.randomUUID();
        Set<String> owners = new HashSet<>();
        owners.add("owner1");
        EnvironmentEntity entity = EnvironmentEntity.builder()
                .owners(owners)
                .build();

        when(environmentRepository.findById(id)).thenReturn(Mono.just(entity));

        StepVerifier.create(environmentService.removeOwnerFromEnvironment(id, "owner1"))
                .expectError(EnvironmentException.class)
                .verify();
    }

    @Test
    void shouldUpdateEnvironment() {
        UUID id = UUID.randomUUID();
        EnvironmentEntity entity = new EnvironmentEntity();
        entity.setId(id);
        entity.setName("old-name");
        entity.setDescription("old-desc");

        EnvironmentRequest request = new EnvironmentRequest();
        request.setName("new-name");
        request.setDescription("new-desc");

        when(environmentRepository.findById(id)).thenReturn(Mono.just(entity));
        when(environmentRepository.save(any(EnvironmentEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(environmentService.updateEnvironment(id, request))
                .verifyComplete();

        assertThat(entity.getName()).isEqualTo("new-name");
        assertThat(entity.getDescription()).isEqualTo("new-desc");
    }

    @Test
    void shouldNotUpdateIfFieldsAreNull() {
        UUID id = UUID.randomUUID();
        EnvironmentEntity entity = new EnvironmentEntity();
        entity.setId(id);
        entity.setName("old-name");
        entity.setDescription("old-desc");

        EnvironmentRequest request = new EnvironmentRequest();
        // request fields are null by default

        when(environmentRepository.findById(id)).thenReturn(Mono.just(entity));
        when(environmentRepository.save(any(EnvironmentEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(environmentService.updateEnvironment(id, request))
                .verifyComplete();

        assertThat(entity.getName()).isEqualTo("old-name");
        assertThat(entity.getDescription()).isEqualTo("old-desc");
    }

    @Test
    void shouldDeleteEnvironment() {
        UUID id = UUID.randomUUID();
        EnvironmentEntity entity = new EnvironmentEntity();

        when(environmentRepository.findById(id)).thenReturn(Mono.just(entity));
        when(environmentRepository.delete(entity)).thenReturn(Mono.empty());

        StepVerifier.create(environmentService.deleteById(id))
                .verifyComplete();

        verify(environmentRepository).delete(entity);
    }
}
