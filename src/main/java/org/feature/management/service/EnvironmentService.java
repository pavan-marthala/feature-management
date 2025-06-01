package org.feature.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.entity.Environment;
import org.feature.management.entity.Owner;
import org.feature.management.exception.ResourceNotFoundException;
import org.feature.management.repository.EnvironmentRepository;
import org.feature.management.repository.OwnerRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class EnvironmentService {
    private final EnvironmentRepository environmentRepo;
    private final OwnerRepository ownerRepo;

    public Environment assignOwnerToEnvironment(Long envId, Long ownerId) {
        log.info("Assigning owner {} to environment {}", ownerId, envId);
        Environment env = environmentRepo.findById(envId).orElseThrow(() -> new ResourceNotFoundException("Environment not found with id: " + envId));
        Owner owner = ownerRepo.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
        env.getOwners().add(owner);
        log.info("Owner {} assigned to environment {}", ownerId, envId);
        return environmentRepo.save(env);
    }

    public Environment removeOwnerFromEnvironment(Long envId, Long ownerId) {
        log.info("Removing owner {} from environment {}", ownerId, envId);
        Environment env = environmentRepo.findById(envId).orElseThrow(() -> new ResourceNotFoundException("Environment not found with id: " + envId));
        Owner owner = ownerRepo.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
        env.getOwners().remove(owner);
        log.info("Owner {} removed from environment {}", ownerId, envId);
        return environmentRepo.save(env);
    }
}
