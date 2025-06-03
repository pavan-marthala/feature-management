package org.feature.management.controller;

import lombok.extern.slf4j.Slf4j;
import org.feature.management.handlers.FeatureApi;
import org.feature.management.models.EnvironmentsIdOwnersPostRequest;
import org.feature.management.models.Feature;
import org.feature.management.models.FeatureRequest;
import org.feature.management.models.FeaturesGet200Response;
import org.feature.management.models.FeaturesIdPatchRequest;
import org.feature.management.service.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/feature")
public class FeatureController {
    @Autowired
    private FeatureService featureService;

    @PutMapping("/{featureId}/owners/{owner}")
    public ResponseEntity<?> addOwnerToFeature(@PathVariable Long featureId, @PathVariable String owner) {
        log.info("Adding owner {} to feature {}", owner, featureId);
        return ResponseEntity.ok(featureService.assignOwnerToFeature(featureId, owner));
    }

    @DeleteMapping("/{featureId}/owners/{ownerId}")
    public ResponseEntity<?> removeOwnerFromFeature(@PathVariable Long featureId, @PathVariable String owner) {
        log.info("Removing owner {} from feature {}", owner, featureId);
        return ResponseEntity.ok(featureService.removeOwnerFromFeature(featureId, owner));
    }

    public ResponseEntity<FeaturesGet200Response> featuresGet(Integer page, Integer size, String _short, String shortBy) {
        Page<Feature> featuresPage = featureService.getAllFeatures(page, size,_short,shortBy);
        FeaturesGet200Response response = new FeaturesGet200Response();
        response.setItems(featuresPage.getContent());
        response.setTotalPages(featuresPage.getTotalPages());
        response.setTotalItems((int) featuresPage.getTotalElements());
        response.setPage(page);
        response.setSize(size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Feature> featuresPost(FeatureRequest featureRequest) {
        log.info("Creating feature: {}", featureRequest);
        Feature createdFeature = featureService.createFeature(featureRequest);
        return new ResponseEntity<>(createdFeature, HttpStatus.CREATED);
    }

    public ResponseEntity<Feature> featuresIdGet(Long id) {
        log.info("Getting feature by ID: {}", id);
        Feature feature = featureService.getById(id);
        return ResponseEntity.ok(feature);
    }

    public ResponseEntity<Void> featuresIdDelete(Long id, Integer ifMatch) {
        log.info("Deleting feature by ID: {}, ifMatch: {}", id, ifMatch);
        featureService.deleteById(id, ifMatch);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
