package org.feature.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.entity.EnvironmentEntity;
import org.feature.management.exception.AccessDeniedException;
import org.feature.management.exception.EnvironmentException;
import org.feature.management.exception.ResourceNotFoundException;
import org.feature.management.mapper.EnvironmentMapper;
import org.feature.management.models.Environment;
import org.feature.management.models.EnvironmentRequest;
import org.feature.management.repository.EnvironmentRepository;
import org.feature.management.utils.SortHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service
@Slf4j
public class EnvironmentService {
    private final EnvironmentRepository environmentRepo;

    public void assignOwnerToEnvironment(UUID envId, String owner) {
        log.debug("Assigning owner {} to environment {}", owner, envId);
        EnvironmentEntity env = getEnvironmentEntity(envId);
        env.getOwners().add(owner);
        log.debug("Owner {} assigned to environment {}", owner, envId);
        environmentRepo.save(env);
    }


    public void removeOwnerFromEnvironment(UUID envId, String ownerId) {
        log.debug("Removing owner {} from environment {}", ownerId, envId);
        EnvironmentEntity env = getEnvironmentEntity(envId);
        Optional.ofNullable(env.getOwners())
                .filter(owners -> owners.contains(ownerId))
                .map(owners -> removeOwner(ownerId, owners))
                .orElseThrow(() -> new AccessDeniedException("Access denied. Only owner of the environment can remove the owner."));

        log.debug("Owner {} removed from environment {}", ownerId, envId);
        environmentRepo.save(env);
    }

    private boolean removeOwner(String ownerId, Set<String> owners) {
        Optional.of(owners).filter(o -> o.size() > 1).orElseThrow(() -> new EnvironmentException("Cannot remove the last owner from environment. At least one owner is required."));
        owners.remove(ownerId);
        return true;
    }

    public UUID createEnvironment(EnvironmentRequest env) {
        log.debug("Creating environment with name: {}", env.getName());
        return environmentRepo.save(EnvironmentMapper.INSTANCE.toEntity(env)).getId();
    }

    public void updateEnvironment(UUID id, EnvironmentRequest request) {
        log.debug("Updating environment with id: {} ", id);
        EnvironmentEntity env = getEnvironmentEntity(id);
        updateIfNotNull(env::setName, request.getName());
        updateIfNotNull(env::setDescription, request.getDescription());
        environmentRepo.save(env);
    }

    private <T> void updateIfNotNull(Consumer<T> setter, T value) {
        Optional.ofNullable(value).ifPresent(setter);
    }

    public void deleteById(UUID id) {
        log.debug("Deleting environment with id: {} ", id);
        EnvironmentEntity env = getEnvironmentEntity(id);
        environmentRepo.delete(env);
    }

    public Environment getById(UUID id) {
        log.debug("Getting environment with id: {}", id);
        return Optional.ofNullable(getEnvironmentEntity(id)).map(EnvironmentMapper.INSTANCE::toModel).orElse(null);
    }

    public Page<Environment> getAllEnvironments(Integer page, Integer size, String sort) {
        log.debug("Getting all environments with page: {} and size: {}", page, size);
        return environmentRepo.findAll(PageRequest.of(page, size, SortHelper.buildSort(sort))).map(EnvironmentMapper.INSTANCE::toModel);
    }

    private EnvironmentEntity getEnvironmentEntity(UUID envId) {
        return environmentRepo.findById(envId).orElseThrow(() -> new ResourceNotFoundException("Environment not found with id: " + envId));
    }
}
