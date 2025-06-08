package org.feature.management.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.entity.EnvironmentEntity;
import org.feature.management.exception.ResourceNotFoundException;
import org.feature.management.models.Environment;
import org.feature.management.repository.EnvironmentRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class EnvironmentService {
    private final EnvironmentRepository environmentRepo;

    public EnvironmentEntity assignOwnerToEnvironment(UUID envId, String  owner) {
        log.info("Assigning owner {} to environment {}", owner, envId);
        EnvironmentEntity env = environmentRepo.findById(envId).orElseThrow(() -> new ResourceNotFoundException("Environment not found with id: " + envId));
        if(env.getOwners() == null){
            env.setOwners(new HashSet<>());
        }
        env.getOwners().add(owner);
        log.info("Owner {} assigned to environment {}", owner, envId);
        return environmentRepo.save(env);
    }

    public EnvironmentEntity removeOwnerFromEnvironment(UUID envId, String ownerId) {
        log.info("Removing owner {} from environment {}", ownerId, envId);
        EnvironmentEntity env = environmentRepo.findById(envId).orElseThrow(() -> new ResourceNotFoundException("Environment not found with id: " + envId));
        if(env.getOwners()!= null && env.getOwners().contains(ownerId)){
            env.getOwners().remove(ownerId);
        } else {
            throw new ResourceNotFoundException("Owner not found in environment: " + envId);
        }
        log.info("Owner {} removed from environment {}", ownerId, envId);
        return environmentRepo.save(env);
    }

    public EnvironmentEntity createEnvironment(@Valid Environment env) {
        return null;
    }
}
