package org.feature.management.feature;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/features")
@RequiredArgsConstructor
public class FeatureController {

    private final FeatureServiceInterface featureService;

    @PostMapping
    public Mono<ResponseEntity<UUID>> createFeature(@Valid @RequestBody Feature featureRequest) {
        log.debug("Creating feature: {}", featureRequest);
        return featureService.createFeature(featureRequest)
                .map(uuid -> {
                    log.info("Feature created with ID: {}", uuid);
                    return ResponseEntity.status(HttpStatus.CREATED).body(uuid);
                });
    }

    @GetMapping
    public Mono<FeatureResponse> getAllFeatures(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "25") Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        log.debug("Getting all features with pagination, page: {}, size: {}", page, size);
        return featureService.getAllFeatures(page, size, sort)
                .map(featuresPage -> FeatureResponse.builder()
                        .totalPages(featuresPage.getTotalPages())
                        .totalItems((int) featuresPage.getTotalElements())
                        .page(page)
                        .size(size)
                        .items(featuresPage.getContent())
                        .build());
    }

    @GetMapping("/strategies")
    public Flux<FeatureStrategyResponseInner> getAllFeatureStrategies() {
        log.debug("Fetching all feature strategies");
        return featureService.getAllFeatureStrategies();
    }

    @GetMapping("/{id}")
    public Mono<Feature> getByFeatureId(@PathVariable("id") String id, @RequestParam(value = "idType", defaultValue = "ID") IdType idType) {
        log.debug("Getting feature by ID: {}", id);
        return featureService.getById(id, idType);
    }

    @PostMapping("/{featureId}/owners/{owner}")
    public Mono<ResponseEntity<Void>> addOwnerToFeature(@PathVariable UUID featureId, @PathVariable String owner) {
        log.debug("Adding owner {} to feature {}", owner, featureId);
        return featureService.assignOwnerToFeature(featureId, owner)
                .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @DeleteMapping("/{featureId}/owners/{owner}")
    public Mono<ResponseEntity<Void>> removeOwnerFromFeature(@PathVariable UUID featureId, @PathVariable String owner) {
        log.debug("Removing owner {} from feature {}", owner, featureId);
        return featureService.removeOwnerFromFeature(featureId, owner)
                .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateFeature(@PathVariable UUID id, @Valid @RequestBody FeatureConfiguration configuration) {
        log.debug("Updating feature by ID: {}", id);
        return featureService.updateFeature(id, configuration)
                .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<Void>> updateFeatureStatus(@PathVariable UUID id, @RequestParam boolean status) {
        log.debug("Updating feature status by ID: {}", id);
        return featureService.updateFeatureStatus(id, status)
                .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteFeatureById(@PathVariable("id") UUID id) {
        log.debug("Deleting feature by ID: {}", id);
        return featureService.deleteById(id)
                .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }
}
