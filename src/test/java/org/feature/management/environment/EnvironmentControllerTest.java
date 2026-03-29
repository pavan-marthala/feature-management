package org.feature.management.environment;

import org.feature.management.feature.FeatureRepository;
import org.feature.management.models.Environment;
import org.feature.management.models.EnvironmentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(EnvironmentController.class)
class EnvironmentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private EnvironmentServiceInterface environmentService;

    @MockitoBean
    private EnvironmentRepository environmentRepository;

    @MockitoBean
    private FeatureRepository featureRepository;

    @Test
    void shouldCreateEnvironment() {
        EnvironmentRequest request = new EnvironmentRequest();
        request.setName("env1");
        request.setOwners(Collections.singletonList("owner1"));
        UUID id = UUID.randomUUID();

        when(environmentService.createEnvironment(any())).thenReturn(Mono.just(id));

        webTestClient.post()
                .uri("/environments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UUID.class).isEqualTo(id);
    }

    @Test
    void shouldGetEnvironmentById() {
        UUID id = UUID.randomUUID();
        Environment environment = new Environment();
        environment.setId(id);
        environment.setName("env1");

        when(environmentService.getById(id)).thenReturn(Mono.just(environment));

        webTestClient.get()
                .uri("/environments/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id.toString())
                .jsonPath("$.name").isEqualTo("env1");
    }

    @Test
    void shouldGetAllEnvironments() {
        Environment environment = new Environment();
        environment.setName("env1");

        when(environmentService.getAllEnvironments(0, 25, null))
                .thenReturn(Mono.just(new PageImpl<>(List.of(environment))));

        webTestClient.get()
                .uri("/environments")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.items[0].name").isEqualTo("env1")
                .jsonPath("$.totalItems").isEqualTo(1);
    }

    @Test
    void shouldAddOwner() {
        UUID id = UUID.randomUUID();
        String owner = "user1";

        when(environmentService.assignOwnerToEnvironment(id, owner)).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/environments/{id}/owners/{owner}", id, owner)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldUpdateEnvironment() {
        UUID id = UUID.randomUUID();
        EnvironmentRequest request = new EnvironmentRequest();
        request.setName("updatedname");
        request.setOwners(Collections.singletonList("owner1"));

        EnvironmentEntity entity = new EnvironmentEntity();
        entity.setEtag(1L);

        when(environmentRepository.findById(id)).thenReturn(Mono.just(entity));
        when(environmentService.updateEnvironment(eq(id), any())).thenReturn(Mono.empty());

        webTestClient.patch()
                .uri("/environments/{id}", id)
                .header("If-Match", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldDeleteEnvironment() {
        UUID id = UUID.randomUUID();
        EnvironmentEntity entity = new EnvironmentEntity();
        entity.setEtag(1L);

        when(environmentRepository.findById(id)).thenReturn(Mono.just(entity));
        when(environmentService.deleteById(id)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/environments/{id}", id)
                .header("If-Match", "1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
