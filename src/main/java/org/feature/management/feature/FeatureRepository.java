package org.feature.management.feature;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface FeatureRepository extends R2dbcRepository<FeatureEntity, UUID> {
    Mono<FeatureEntity> getByName(String name);
    Mono<Boolean> existsByName(String name);
    Flux<FeatureEntity> findBy(Pageable pageable);
}
