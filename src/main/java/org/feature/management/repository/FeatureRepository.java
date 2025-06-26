package org.feature.management.repository;

import org.feature.management.entity.FeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeatureRepository extends JpaRepository<FeatureEntity, UUID> {
    Optional<FeatureEntity> getByName(String name);

    boolean existsByName(String name);
}
