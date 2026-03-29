package org.feature.management.feature;
import org.feature.management.environment.EnvironmentRepository;
import java.util.Collections;

import org.feature.management.models.Feature;
import org.feature.management.models.BooleanFeatureStrategy;
import org.feature.management.models.FeatureStrategy;
import org.feature.management.models.FeatureStrategyResponseInner;
import org.feature.management.models.IdType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(FeatureController.class)
class FeatureControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FeatureServiceInterface featureService;

    @MockitoBean
    private FeatureRepository featureRepository;

    @MockitoBean
    private EnvironmentRepository environmentRepository;

    @Test
    void shouldCreateFeature() {
        Feature request = new Feature();
        request.setName("feature1");
        request.setOwners(Collections.singletonList("owner1"));
        UUID id = UUID.randomUUID();

        when(featureService.createFeature(any())).thenReturn(Mono.just(id));

        webTestClient.post()
                .uri("/features")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UUID.class).isEqualTo(id);
    }

    @Test
    void shouldGetFeatureById() {
        UUID id = UUID.randomUUID();
        Feature feature = new Feature();
        feature.setId(id);
        feature.setName("feature-1");

        when(featureService.getById(id.toString(), IdType.ID)).thenReturn(Mono.just(feature));

        webTestClient.get()
                .uri("/features/{id}?idType=ID", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id.toString())
                .jsonPath("$.name").isEqualTo("feature-1");
    }

    @Test
    void shouldGetAllFeatures() {
        Feature feature = new Feature();
        feature.setName("feature-1");

        when(featureService.getAllFeatures(0, 25, null))
                .thenReturn(Mono.just(new PageImpl<>(List.of(feature))));

        webTestClient.get()
                .uri("/features")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.items[0].name").isEqualTo("feature-1")
                .jsonPath("$.totalItems").isEqualTo(1);
    }

    @Test
    void shouldGetAllStrategies() {
        FeatureStrategyResponseInner strategy = new FeatureStrategyResponseInner();
        strategy.setName(FeatureStrategy.BOOLEAN_FEATURE_STRATEGY);

        when(featureService.getAllFeatureStrategies()).thenReturn(Flux.just(strategy));

        webTestClient.get()
                .uri("/features/strategies")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("BooleanFeatureStrategy");
    }

    @Test
    void shouldUpdateFeature() {
        UUID id = UUID.randomUUID();
        BooleanFeatureStrategy config = new BooleanFeatureStrategy();

        FeatureEntity entity = new FeatureEntity();
        entity.setEtag(1L);

        when(featureRepository.findById(id)).thenReturn(Mono.just(entity));
        when(featureService.updateFeature(eq(id), any())).thenReturn(Mono.empty());

        webTestClient.patch()
                .uri("/features/{id}", id)
                .header("If-Match", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(config)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldDeleteFeature() {
        UUID id = UUID.randomUUID();
        FeatureEntity entity = new FeatureEntity();
        entity.setEtag(1L);

        when(featureRepository.findById(id)).thenReturn(Mono.just(entity));
        when(featureService.deleteById(id)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/features/{id}", id)
                .header("If-Match", "1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
