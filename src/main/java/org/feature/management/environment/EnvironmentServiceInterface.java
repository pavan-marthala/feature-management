package org.feature.management.environment;

import org.feature.management.models.Environment;
import org.feature.management.models.EnvironmentRequest;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EnvironmentServiceInterface {
    Mono<Void> assignOwnerToEnvironment(UUID envId, String owner);

    Mono<Void> removeOwnerFromEnvironment(UUID envId, String ownerId);

    Mono<UUID> createEnvironment(EnvironmentRequest env);

    Mono<Void> updateEnvironment(UUID id, EnvironmentRequest request);

    Mono<Void> deleteById(UUID id);

    Mono<Environment> getById(UUID id);

    Mono<Page<Environment>> getAllEnvironments(Integer page, Integer size, String sort);
}
