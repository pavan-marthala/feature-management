package org.feature.management.environment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.models.Environment;
import org.feature.management.models.EnvironmentRequest;
import org.feature.management.shared.exception.AccessDeniedException;
import org.feature.management.shared.exception.EnvironmentException;
import org.feature.management.shared.exception.ResourceNotFoundException;
import org.feature.management.shared.utils.SortHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnvironmentService implements EnvironmentServiceInterface {

    private final EnvironmentRepository environmentRepository;

    @Override
    public Mono<Void> assignOwnerToEnvironment(UUID envId, String owner) {
        log.debug("Assigning owner {} to environment {}", owner, envId);
        return getEnvironmentEntity(envId)
                .map(entity -> {
                    entity.getOwners().add(owner);
                    return entity;
                })
                .flatMap(environmentRepository::save)
                .then();
    }

    @Override
    public Mono<Void> removeOwnerFromEnvironment(UUID envId, String ownerId) {
        log.debug("Removing owner {} from environment {}", ownerId, envId);
        return getEnvironmentEntity(envId)
                .flatMap(entity -> {
                    Set<String> owners = entity.getOwners();
                    return Mono.just(entity)
                            .filter(_ -> owners != null && owners.contains(ownerId))
                            .switchIfEmpty(Mono.error(new AccessDeniedException("Access denied. Only owner of the environment can remove the owner.")))
                            .filter(_ -> owners.size() > 1)
                            .switchIfEmpty(Mono.error(new EnvironmentException("Cannot remove the last owner from environment. At least one owner is required.")))
                            .flatMap(_ -> {
                                owners.remove(ownerId);
                                return environmentRepository.save(entity);
                            });
                })
                .then();
    }
    private <T> void updateIfNotNull(Consumer<T> setter, T value) {
        Optional.ofNullable(value).ifPresent(setter);
    }

    @Override
    public Mono<UUID> createEnvironment(EnvironmentRequest env) {
        log.debug("Creating environment with name: {}", env.getName());
        EnvironmentEntity entity = EnvironmentMapper.INSTANCE.toEntity(env);
        return environmentRepository.save(entity)
                .map(EnvironmentEntity::getId);
    }

    @Override
    public Mono<Void> updateEnvironment(UUID id, EnvironmentRequest request) {
        log.debug("Updating environment with id: {} ", id);
        return getEnvironmentEntity(id)
                .map(env -> {
                    updateIfNotNull(env::setName, request.getName());
                    updateIfNotNull(env::setDescription, request.getDescription());
                    return env;
                })
                .flatMap(environmentRepository::save)
                .then();
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        log.debug("Deleting environment with id: {} ", id);
        return getEnvironmentEntity(id)
                .flatMap(environmentRepository::delete)
                .then();
    }

    @Override
    public Mono<Environment> getById(UUID id) {
        log.debug("Getting environment with id: {}", id);
        return getEnvironmentEntity(id)
                .map(EnvironmentMapper.INSTANCE::toModel);
    }

    @Override
    public Mono<Page<Environment>> getAllEnvironments(Integer page, Integer size, String sort) {
        log.debug("Getting all environments with page: {} and size: {}", page, size);
        PageRequest pageRequest = PageRequest.of(page, size, SortHelper.buildSort(sort));
        return environmentRepository.findBy(pageRequest)
                .map(EnvironmentMapper.INSTANCE::toModel)
                .collectList()
                .zipWith(environmentRepository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageRequest, tuple.getT2()));
    }

    private Mono<EnvironmentEntity> getEnvironmentEntity(UUID envId) {
        return environmentRepository.findById(envId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Environment not found with id: " + envId)));
    }
}
