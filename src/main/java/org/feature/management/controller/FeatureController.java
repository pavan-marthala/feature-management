package org.feature.management.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.entity.FeatureEntity;
import org.feature.management.models.Feature;
import org.feature.management.models.FeaturesGet200Response;
import org.feature.management.service.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/feature")
public class FeatureController {
    @Autowired
    private FeatureService featureService;

    @PutMapping("/{featureId}/owners/{owner}")
    public ResponseEntity<?> addOwnerToFeature(@PathVariable UUID featureId, @PathVariable String owner) {
        log.info("Adding owner {} to feature {}", owner, featureId);
        return ResponseEntity.ok(featureService.assignOwnerToFeature(featureId, owner));
    }

    @DeleteMapping("/{featureId}/owners/{owner}")
    public ResponseEntity<?> removeOwnerFromFeature(@PathVariable UUID featureId, @PathVariable String owner) {
        log.info("Removing owner {} from feature {}", owner, featureId);
        return ResponseEntity.ok(featureService.removeOwnerFromFeature(featureId, owner));
    }
    @GetMapping
    public ResponseEntity<List<FeatureEntity>> featuresGet(@RequestParam(value = "page",defaultValue = "0") Integer page,@RequestParam(value = "size",defaultValue = "40") Integer size) {
        Page<FeatureEntity> featuresPage = featureService.getAllFeatures(page, size);
//        FeaturesGet200Response response = new FeaturesGet200Response();
//        response.setItems(featuresPage.getContent());
//        response.setTotalPages(featuresPage.getTotalPages());
//        response.setTotalItems((int) featuresPage.getTotalElements());
//        response.setPage(page);
//        response.setSize(size);
        return new ResponseEntity<>(featuresPage.getContent(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UUID> featuresPost(@Valid @RequestBody FeatureEntity featureRequest) {
        log.info("Creating feature: {}", featureRequest);
        UUID uuid = featureService.createFeature(featureRequest);
        return new ResponseEntity<>(uuid, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public FeatureEntity featuresIdGet(@PathVariable("id") UUID id) {
        log.info("Getting feature by ID: {}", id);
        return featureService.getById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> featuresIdDelete(@PathVariable("id") UUID id, @RequestHeader(value = "If-Match") Integer ifMatch) {
        log.info("Deleting feature by ID: {}, ifMatch: {}", id, ifMatch);
        featureService.deleteById(id, ifMatch);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
