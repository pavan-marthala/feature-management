package org.feature.management.interfaces.service;

import org.feature.management.models.Environment;
import org.feature.management.models.EnvironmentRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface EnvironmentServiceInterface {
    void assignOwnerToEnvironment(UUID envId, String owner);

    void removeOwnerFromEnvironment(UUID envId, String ownerId);

    UUID createEnvironment(EnvironmentRequest env);

    void updateEnvironment(UUID id, EnvironmentRequest request);

    void deleteById(UUID id);

    Environment getById(UUID id);

    Page<Environment> getAllEnvironments(Integer page, Integer size, String sort);
}
