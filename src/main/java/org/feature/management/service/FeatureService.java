package org.feature.management.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.entity.Environment;
import org.feature.management.entity.Feature;
import org.feature.management.entity.FeatureStrategy;
import org.feature.management.entity.Owner;
import org.feature.management.exception.ResourceNotFoundException;
import org.feature.management.models.FeatureRequest;
import org.feature.management.models.FeatureRequestOwnersInner;
import org.feature.management.models.FeatureRequestStrategiesInner;
import org.feature.management.repository.EnvironmentRepository;
import org.feature.management.repository.FeatureRepository;
import org.feature.management.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeatureService {

    private final FeatureRepository featureRepo;
    private final OwnerRepository ownerRepo;
    private final ObjectMapper mapper;

    public Feature assignOwnerToFeature(Long featureId, Long ownerId) {
        log.info("Assigning owner {} to feature {}", ownerId, featureId);
        Feature feature = featureRepo.findById(featureId).orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + featureId));
        Owner owner = ownerRepo.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
        feature.getOwners().add(owner);
        log.info("Owner {} assigned to feature {}", ownerId, featureId);
        return featureRepo.save(feature);
    }

    public Feature removeOwnerFromFeature(Long featureId, Long ownerId) {
        log.info("Removing owner {} from feature {}", ownerId, featureId);
        Feature feature = featureRepo.findById(featureId).orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + featureId));
        Owner owner = ownerRepo.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
        feature.getOwners().remove(owner);
        log.info("Owner {} removed from feature {}", ownerId, featureId);
        return featureRepo.save(feature);
    }

    public Page<org.feature.management.models.Feature> getAllFeatures(Integer page, Integer size, String _short, String shortBy) {
        log.info("Fetching features with pagination, page: {}, size: {}, short: {}, sortBy: {}", page, size, _short, shortBy);
        Pageable pageable = PageRequest.of(page, size, _short.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, shortBy);
        Page<Feature> featuresPage = featureRepo.findAll(pageable);

        List<org.feature.management.models.Feature> mappedList = featuresPage
                .stream()
                .map(feature -> mapper.convertValue(feature, org.feature.management.models.Feature.class))
                .toList();

        return new PageImpl<>(mappedList, pageable, featuresPage.getTotalElements());
    }


    @Transactional
    public org.feature.management.models.Feature createFeature(FeatureRequest featureRequest) {
        log.info("Creating feature with request: {}", featureRequest);

        Feature feature = Feature.builder()
                .name(featureRequest.getName())
                .description(featureRequest.getDescription())
                .enabled(featureRequest.getEnabled())
                .owners(new HashSet<>())
                .strategies(buildStrategies(featureRequest))
                .build();
        feature.getStrategies().forEach(strategy -> strategy.setFeature(feature));

        Set<Owner> owners = processOwners(featureRequest.getOwners(), feature);
        feature.setOwners(owners);

        Feature savedFeature = featureRepo.save(feature);
        log.info("Feature created with ID: {}", savedFeature.getId());

        return mapper.convertValue(savedFeature, org.feature.management.models.Feature.class);
    }


    private List<FeatureStrategy> buildStrategies(FeatureRequest featureRequest) {
        log.info("Building strategies for feature: {}", featureRequest.getName());
        return featureRequest.getStrategies().stream()
                .map(req -> mapper.convertValue(req, FeatureStrategy.class))
                .collect(Collectors.toList());
    }

    public org.feature.management.models.Feature getById(Long id) {
        log.info("Fetching feature by id: {}", id);
        Feature feature = featureRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + id));
        return mapper.convertValue(feature, org.feature.management.models.Feature.class);
    }
    private Set<Owner> processOwners(List<FeatureRequestOwnersInner> ownerRequests, Feature feature) {
        log.info("Processing owners for feature: {}", feature.getId());
        Set<String> emails = ownerRequests.stream()
                .map(FeatureRequestOwnersInner::getEmail)
                .collect(Collectors.toSet());

        Map<String, Owner> existingOwners = ownerRepo.findByEmailIn(emails).stream()
                .collect(Collectors.toMap(Owner::getEmail, Function.identity()));
        log.info("Found {} existing owners: {}", existingOwners.size(), existingOwners.keySet());
        return ownerRequests.stream()
                .map(req -> {
                    Owner owner = existingOwners.get(req.getEmail());
                    if (owner == null) {
                        owner = mapper.convertValue(req, Owner.class);
                        owner.setFeatures(new HashSet<>());
                    }
                    if (owner.getFeatures() == null) {
                        owner.setFeatures(new HashSet<>());
                    }
                    owner.getFeatures().add(feature);
                    return owner;
                })
                .collect(Collectors.toSet());
    }

    public void deleteById(Long id, Integer ifMatch) {
        log.info("Deleting feature by id: {}, ifMatch: {}", id, ifMatch);
        Feature feature = featureRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + id));
        featureRepo.delete(feature);
    }
}
