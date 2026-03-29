package org.feature.management.environment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface EnvironmentRepository extends R2dbcRepository<EnvironmentEntity, UUID> {
    Flux<EnvironmentEntity> findBy(Pageable pageable);
}
