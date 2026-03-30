package org.feature.management.dashboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.environment.EnvironmentRepository;
import org.feature.management.feature.FeatureRepository;
import org.feature.management.models.DashboardStats;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {

    private final FeatureRepository featureRepository;
    private final EnvironmentRepository environmentRepository;

    public Mono<DashboardStats> getDashboardStats() {
        log.debug("Fetching dashboard statistics");
        return Mono.zip(
                featureRepository.count(),
                featureRepository.countByEnabledTrue(),
                featureRepository.countByEnabledFalse(),
                environmentRepository.count()
        ).map(tuple -> DashboardStats.builder()
                .totalFeatures(tuple.getT1())
                .activeFeatures(tuple.getT2())
                .disabledFeatures(tuple.getT3())
                .totalEnvironments(tuple.getT4())
                .build());
    }
}
