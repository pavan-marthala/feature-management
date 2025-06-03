package org.feature.management.repository;

import org.feature.management.entity.EnvironmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvironmentRepository extends JpaRepository<EnvironmentEntity, Long> {}
