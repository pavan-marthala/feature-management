package org.feature.management.feature;

import org.feature.management.models.Feature;
import org.feature.management.models.FeatureConfiguration;
import org.feature.management.models.FeatureStrategyResponseInner;
import org.feature.management.models.IdType;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface FeatureServiceInterface {

    Mono<Void> assignOwnerToFeature(UUID featureId, String owner);

    Mono<Void> removeOwnerFromFeature(UUID featureId, String owner);

    Mono<Page<Feature>> getAllFeatures(Integer page, Integer size, String sort);

    Mono<UUID> createFeature(Feature featureRequest);

    Mono<Feature> getById(String id, IdType idType);

    Mono<Void> deleteById(UUID id);

    Flux<FeatureStrategyResponseInner> getAllFeatureStrategies();

    Mono<Feature> getFeatureByName(String name);

    Mono<Void> updateFeature(UUID id, FeatureConfiguration configuration);

}
