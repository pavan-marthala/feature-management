package org.feature.management.interfaces.service;

import org.feature.management.models.Feature;
import org.feature.management.models.FeatureStrategyResponseInner;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface FeatureServiceInterface {

    void assignOwnerToFeature(UUID featureId, String owner);

    void removeOwnerFromFeature(UUID featureId, String owner);

    Page<Feature> getAllFeatures(Integer page, Integer size, String sort);

    UUID createFeature(Feature featureRequest);

    Feature getById(UUID id);

    void deleteById(UUID id);

    List<FeatureStrategyResponseInner> getAllFeatureStrategies();

    Feature getFeatureByName(String name);

}
