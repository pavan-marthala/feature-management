package org.feature.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.interfaces.service.FeatureServiceInterface;
import org.feature.management.models.Feature;
import org.feature.management.models.FeatureConfiguration;
import org.feature.management.models.FeatureResponse;
import org.feature.management.models.FeatureStrategyResponseInner;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/features")
@RequiredArgsConstructor
public class FeatureController {
    private final FeatureServiceInterface featureService;

    @PostMapping
    public ResponseEntity<UUID> createFeature(@Valid @RequestBody Feature featureRequest) {
        log.debug("Creating feature: {}", featureRequest);
        UUID uuid = featureService.createFeature(featureRequest);
        log.info("Feature created with ID: {}", uuid);
        return new ResponseEntity<>(uuid, HttpStatus.CREATED);
    }

    @GetMapping
    public FeatureResponse getAllFeatures(@RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam(value = "size", defaultValue = "25") Integer size, @RequestParam(value = "sort", required = false) String sort) {
        log.debug("Getting all features with pagination, page: {}, size: {}", page, size);
        Page<Feature> featuresPage = featureService.getAllFeatures(page, size, sort);
        return FeatureResponse.builder().totalPages(featuresPage.getTotalPages()).totalItems((int) featuresPage.getTotalElements()).page(page).size(size).items(featuresPage.getContent()).build();
    }

    @GetMapping("/strategies")
    public List<FeatureStrategyResponseInner> getAllFeatureStrategies() {
        log.debug("Fetching all feature strategies");
        return featureService.getAllFeatureStrategies();
    }

    @GetMapping("/{id}")
    public Feature getByFeatureId(@PathVariable("id") UUID id) {
        log.debug("Getting feature by ID: {}", id);
        return featureService.getById(id);
    }

    @GetMapping("/getByName/{name}")
    public Feature getByFeatureByName(@PathVariable("name") String name) {
        log.debug("Getting feature by name: {}", name);
        return featureService.getFeatureByName(name);
    }

    @PostMapping("/{featureId}/owners/{owner}")
    public ResponseEntity<?> addOwnerToFeature(@PathVariable UUID featureId, @PathVariable String owner) {
        log.debug("Adding owner {} to feature {}", owner, featureId);
        featureService.assignOwnerToFeature(featureId, owner);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{featureId}/owners/{owner}")
    public ResponseEntity<?> removeOwnerFromFeature(@PathVariable UUID featureId, @PathVariable String owner) {
        log.debug("Removing owner {} from feature {}", owner, featureId);
        featureService.removeOwnerFromFeature(featureId, owner);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateFeature(@PathVariable UUID id, @Valid @RequestBody FeatureConfiguration configuration) {
        log.debug("Updating feature by ID: {}", id);
        featureService.updateFeature(id, configuration);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeatureById(@PathVariable("id") UUID id) {
        log.debug("Deleting feature by ID: {}", id);
        featureService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
