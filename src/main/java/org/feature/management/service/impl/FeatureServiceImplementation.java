package org.feature.management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.config.FeatureStrategyConfig;
import org.feature.management.entity.FeatureEntity;
import org.feature.management.exception.AccessDeniedException;
import org.feature.management.exception.EnvironmentException;
import org.feature.management.exception.FeatureException;
import org.feature.management.exception.ResourceNotFoundException;
import org.feature.management.mapper.FeatureMapper;
import org.feature.management.models.Feature;
import org.feature.management.models.FeatureConfiguration;
import org.feature.management.models.FeatureStrategyResponseInner;
import org.feature.management.models.IdType;
import org.feature.management.repository.FeatureRepository;
import org.feature.management.service.FeatureServiceInterface;
import org.feature.management.utils.SortHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeatureServiceImplementation implements FeatureServiceInterface {

    private final FeatureRepository featureRepo;
    private final FeatureStrategyConfig featureStrategyConfig;

    @Override
    public void assignOwnerToFeature(UUID featureId, String owner) {
        log.debug("Assigning owner {} to feature {}", owner, featureId);
        FeatureEntity feature = getFeature(featureId);
        feature.getOwners().add(owner);
        log.debug("Owner {} assigned to feature {}", owner, featureId);
        featureRepo.save(feature);
    }

    @Override
    public void removeOwnerFromFeature(UUID featureId, String owner) {
        log.debug("Removing owner {} from feature {}", owner, featureId);
        FeatureEntity feature = getFeature(featureId);
        Optional.ofNullable(feature.getOwners())
                .filter(owners -> owners.contains(owner))
                .map(owners -> removeOwner(owner, owners))
                .orElseThrow(() -> new AccessDeniedException("Access denied. Only owner of the feature can remove the owner."));

        log.debug("Owner {} removed from feature {}", owner, featureId);
        featureRepo.save(feature);
    }

    private boolean removeOwner(String ownerId, Set<String> owners) {
        Optional.of(owners).filter(o -> o.size() > 1).orElseThrow(() -> new EnvironmentException("Cannot remove the last owner from environment. At least one owner is required."));
        owners.remove(ownerId);
        return true;
    }

    @Override
    public Page<Feature> getAllFeatures(Integer page, Integer size, String sort) {
        log.debug("Fetching features with pagination, page: {}, size: {}", page, size);
        return featureRepo.findAll(PageRequest.of(page, size, SortHelper.buildSort(sort))).map(FeatureMapper.INSTANCE::toModel);
    }

    @Override
    @Transactional
    public UUID createFeature(Feature featureRequest) {
        Optional.ofNullable(featureRequest.getName()).filter(featureRepo::existsByName)
                .ifPresent(name -> {
                    throw new FeatureException("Feature with name " + name + " already exists");
                });
        log.debug("Creating feature with request: {}", featureRequest);
        FeatureEntity savedFeature = featureRepo.save(FeatureMapper.INSTANCE.toEntity(featureRequest));
        log.debug("Feature created with ID: {}", savedFeature.getId());
        return savedFeature.getId();
    }

    @Override
    public Feature getById(String id, IdType idType) {
        log.debug("Fetching feature by id: {}", id);
        return Optional.ofNullable(idType == IdType.ID ? getFeature(UUID.fromString(id)) : featureRepo.getByName(id).orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + id))).map(FeatureMapper.INSTANCE::toModel).orElse(null);
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting feature by id: {}", id);
        FeatureEntity feature = getFeature(id);
        featureRepo.delete(feature);
    }

    @Override
    public List<FeatureStrategyResponseInner> getAllFeatureStrategies() {
        log.debug("Fetching all feature strategies");
        return featureStrategyConfig.getStrategies();
    }

    @Override
    public Feature getFeatureByName(String name) {
        log.debug("Fetching feature by name: {}", name);
        return featureRepo.getByName(name).map(FeatureMapper.INSTANCE::toModel).orElse(null);
    }

    @Override
    public void updateFeature(UUID id, FeatureConfiguration configuration) {
        log.debug("Updating feature configuration with id: {}", id);
        FeatureEntity feature = getFeature(id);
        feature.setConfiguration(configuration);
        featureRepo.save(feature);
    }

    private FeatureEntity getFeature(UUID featureId) {
        return featureRepo.findById(featureId).orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + featureId));
    }
}
