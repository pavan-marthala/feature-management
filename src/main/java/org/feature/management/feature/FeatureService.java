package org.feature.management.feature;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.config.FeatureStrategyConfig;
import org.feature.management.models.Feature;
import org.feature.management.models.FeatureConfiguration;
import org.feature.management.models.FeatureStrategyResponseInner;
import org.feature.management.models.IdType;
import org.feature.management.shared.exception.AccessDeniedException;
import org.feature.management.shared.exception.EnvironmentException;
import org.feature.management.shared.exception.FeatureException;
import org.feature.management.shared.exception.ResourceNotFoundException;
import org.feature.management.shared.utils.SortHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeatureService implements FeatureServiceInterface {

    private final FeatureRepository featureRepo;
    private final FeatureStrategyConfig featureStrategyConfig;

    @Override
    public Mono<Void> assignOwnerToFeature(UUID featureId, String owner) {
        log.debug("Assigning owner {} to feature {}", owner, featureId);
        return getFeatureEntity(featureId)
                .map(feature -> {
                    feature.getOwners().add(owner);
                    return feature;
                })
                .flatMap(featureRepo::save)
                .then();
    }

    @Override
public Mono<Void> removeOwnerFromFeature(UUID featureId, String owner) {
    log.debug("Removing owner {} from feature {}", owner, featureId);
    return getFeatureEntity(featureId)
            .flatMap(feature -> {
                Set<String> owners = feature.getOwners();
                return Mono.just(feature)
                        .filter(_ -> owners != null && owners.contains(owner))
                        .switchIfEmpty(Mono.error(new AccessDeniedException("Access denied. Only owner of the feature can remove the owner.")))
                        .filter(_ -> owners.size() > 1)
                        .switchIfEmpty(Mono.error(new EnvironmentException("Cannot remove the last owner from feature. At least one owner is required.")))
                        .flatMap(_ -> {
                            owners.remove(owner);
                            return featureRepo.save(feature);
                        });
            })
            .then();
}

    @Override
    public Mono<Page<Feature>> getAllFeatures(Integer page, Integer size, String sort) {
        log.debug("Fetching features with pagination, page: {}, size: {}", page, size);
        PageRequest pageRequest = PageRequest.of(page, size, SortHelper.buildSort(sort));
        return featureRepo.findBy(pageRequest)
                .map(FeatureMapper.INSTANCE::toModel)
                .collectList()
                .zipWith(featureRepo.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageRequest, tuple.getT2()));
    }

    @Override
    @Transactional
    public Mono<UUID> createFeature(Feature featureRequest) {
        log.debug("Creating feature with request: {}", featureRequest);
        return featureRepo.existsByName(featureRequest.getName())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new FeatureException("Feature with name " + featureRequest.getName() + " already exists")))
                .then(Mono.fromCallable(() -> FeatureMapper.INSTANCE.toEntity(featureRequest)))
                .flatMap(featureRepo::save)
                .map(FeatureEntity::getId);
    }

    @Override
    public Mono<Feature> getById(String id, IdType idType) {
        log.debug("Fetching feature by id: {} with type: {}", id, idType);
        if (idType == IdType.ID) {
            return getFeatureEntity(UUID.fromString(id))
                    .map(FeatureMapper.INSTANCE::toModel);
        } else {
            return featureRepo.getByName(id)
                    .map(FeatureMapper.INSTANCE::toModel)
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException("Feature not found with name: " + id)));
        }
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        log.debug("Deleting feature by id: {}", id);
        return getFeatureEntity(id)
                .flatMap(featureRepo::delete)
                .then();
    }

    @Override
    public Flux<FeatureStrategyResponseInner> getAllFeatureStrategies() {
        log.debug("Fetching all feature strategies");
        return Flux.fromIterable(featureStrategyConfig.getStrategies());
    }

    @Override
    public Mono<Feature> getFeatureByName(String name) {
        log.debug("Fetching feature by name: {}", name);
        return featureRepo.getByName(name)
                .map(FeatureMapper.INSTANCE::toModel);
    }

    @Override
    public Mono<Void> updateFeature(UUID id, FeatureConfiguration configuration) {
        log.debug("Updating feature configuration with id: {}", id);
        return getFeatureEntity(id)
                .map(feature -> {
                    feature.setConfiguration(configuration);
                    return feature;
                })
                .flatMap(featureRepo::save)
                .then();
    }

    @Override
    public Mono<Void> updateFeatureStatus(UUID id, boolean enabled) {
        log.debug("Updating feature status with id: {} to enabled: {}", id, enabled);
        return getFeatureEntity(id)
                .map(feature -> {
                    feature.setEnabled(enabled);
                    return feature;
                })
                .flatMap(featureRepo::save)
                .then();
    }

    private Mono<FeatureEntity> getFeatureEntity(UUID featureId) {
        return featureRepo.findById(featureId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Feature not found with id: " + featureId)));
    }
}
