package org.feature.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.entity.EnvironmentEntity;
import org.feature.management.exception.EnvironmentException;
import org.feature.management.exception.ResourceNotFoundException;
import org.feature.management.mapper.EnvironmentMapper;
import org.feature.management.models.Environment;
import org.feature.management.models.EnvironmentRequest;
import org.feature.management.models.EnvironmentsIdPatchRequest;
import org.feature.management.repository.EnvironmentRepository;
import org.feature.management.utils.ValidationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service
@Slf4j
public class EnvironmentService {
    private final EnvironmentRepository environmentRepo;
    private final EnvironmentMapper environmentMapper;

    public void assignOwnerToEnvironment(UUID envId, String owner) {
        log.info("Assigning owner {} to environment {}", owner, envId);
        EnvironmentEntity env = environmentRepo.findById(envId).orElseThrow(() -> new ResourceNotFoundException("Environment not found with id: " + envId));
        var owners = Optional.ofNullable(env.getOwners()).orElse(new HashSet<>());
        owners.add(owner);
        env.setOwners(owners);
        log.info("Owner {} assigned to environment {}", owner, envId);
        environmentRepo.save(env);
    }

    public void removeOwnerFromEnvironment(UUID envId, String ownerId,Long ifMatch) {
        log.info("Removing owner {} from environment {}", ownerId, envId);
        EnvironmentEntity env = environmentRepo.findById(envId).orElseThrow(() -> new ResourceNotFoundException("Environment not found with id: " + envId));

        ValidationUtils.validateETagMatch(env.getEtag(), ifMatch, () -> new EnvironmentException("Version mismatch. Please provide the correct if-match header."));

        Optional.ofNullable(env.getOwners())
                .filter(owners -> owners.contains(ownerId))
                .map(owners -> {
                    Optional.of(owners).filter(o -> o.size() > 1).orElseThrow(() -> new EnvironmentException("Cannot remove the last owner from environment. At least one owner is required."));
                    owners.remove(ownerId);
                    return true;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found in environment: " + envId));

        log.info("Owner {} removed from environment {}", ownerId, envId);
        environmentRepo.save(env);
    }

    public UUID createEnvironment(EnvironmentRequest env) {
        log.info("Creating environment with name: {}", env.getName());
        EnvironmentEntity environmentEntity = EnvironmentEntity.builder().name(env.getName()).description(env.getDescription()).owners(new HashSet<>(env.getOwners())).build();
        return environmentRepo.save(environmentEntity).getId();
    }

    public void updateEnvironment(UUID id, Long ifMatch, EnvironmentsIdPatchRequest request) {
        log.info("Updating environment with id: {} and ifMatch: {}", id, ifMatch);
        EnvironmentEntity env = environmentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Environment not found with id: " + id));
        ValidationUtils.validateETagMatch(env.getEtag(), ifMatch, () -> new EnvironmentException("Version mismatch. Please provide the correct if-match header."));
        updateIfNotNull(env::setName, request.getName());
        updateIfNotNull(env::setDescription, request.getDescription());
        environmentRepo.save(env);
    }

    private <T> void updateIfNotNull(Consumer<T> setter,T value){
        Optional.ofNullable(value).ifPresent(setter);
    }

    public void deleteById(UUID id, Long ifMatch) {
        log.info("Deleting environment with id: {} and ifMatch: {}", id, ifMatch);
        EnvironmentEntity env = environmentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Environment not found with id: " + id));
        ValidationUtils.validateETagMatch(env.getEtag(), ifMatch, () -> new EnvironmentException("Version mismatch. Please provide the correct if-match header."));
        environmentRepo.deleteById(id);
    }

    public Environment getById(UUID id) {
        log.info("Getting environment with id: {}", id);
        return environmentRepo.findById(id).map(environmentMapper::toDto).orElseThrow(() -> new ResourceNotFoundException("Environment not found with id: " + id));
    }

    public Page<Environment> getAllEnvironments(Integer page, Integer size) {
        log.info("Getting all environments with page: {} and size: {}", page, size);
        return environmentRepo.findAll(PageRequest.of(page, size)).map(environmentMapper::toDto);
    }
}
