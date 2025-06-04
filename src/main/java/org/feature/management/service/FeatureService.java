package org.feature.management.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.entity.FeatureEntity;
import org.feature.management.exception.ResourceNotFoundException;
import org.feature.management.repository.FeatureRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeatureService {

    private final FeatureRepository featureRepo;
    private final ObjectMapper mapper;

    public FeatureEntity assignOwnerToFeature(UUID featureId, String owner) {
        log.info("Assigning owner {} to feature {}", owner, featureId);
        FeatureEntity feature = featureRepo.findById(featureId).orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + featureId));
        if(feature.getOwners() == null){
            feature.setOwners(new HashSet<>());
        }
        feature.getOwners().add(owner);
        log.info("Owner {} assigned to feature {}", owner, featureId);
        return featureRepo.save(feature);
    }

    public FeatureEntity removeOwnerFromFeature(UUID featureId, String owner) {
        log.info("Removing owner {} from feature {}", owner, featureId);
        FeatureEntity feature = featureRepo.findById(featureId).orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + featureId));
        if(feature.getOwners()!= null && feature.getOwners().contains(owner)){
            feature.getOwners().remove(owner);
        } else {
            throw new ResourceNotFoundException("Owner not found in environment: " + featureId);
        }
        log.info("Owner {} removed from feature {}", owner, featureId);
        return featureRepo.save(feature);
    }

    public Page<FeatureEntity> getAllFeatures(Integer page, Integer size, String _short, String shortBy) {
        log.info("Fetching features with pagination, page: {}, size: {}, short: {}, sortBy: {}", page, size, _short, shortBy);
        Pageable pageable = PageRequest.of(page, size, _short.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, shortBy);
        Page<FeatureEntity> featuresPage = featureRepo.findAll(pageable);

        List<FeatureEntity> mappedList = featuresPage
                .stream()
               // .map(feature -> mapper.convertValue(feature, org.feature.management.models.Feature.class))
                .toList();

        return new PageImpl<>(mappedList, pageable, featuresPage.getTotalElements());
    }


    @Transactional
    public UUID createFeature(FeatureEntity featureRequest) {
        log.info("Creating feature with request: {}", featureRequest);

        FeatureEntity feature = FeatureEntity.builder()
                .enabled(featureRequest.isEnabled())
                .owners(featureRequest.getOwners())
                .strategy(featureRequest.getStrategy())
                .build();
        if (feature.getStrategy() != null) {
            feature.getStrategy().setFeature(feature);
        }
        FeatureEntity savedFeature = featureRepo.save(feature);
        log.info("Feature created with ID: {}", savedFeature.getId());

        return savedFeature.getId();
    }


    public org.feature.management.models.Feature getById(UUID id) {
        log.info("Fetching feature by id: {}", id);
        FeatureEntity feature = featureRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + id));
        return mapper.convertValue(feature, org.feature.management.models.Feature.class);
    }

    public void deleteById(UUID id, Integer ifMatch) {
        log.info("Deleting feature by id: {}, ifMatch: {}", id, ifMatch);
        FeatureEntity feature = featureRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + id));
        featureRepo.delete(feature);
    }
}
