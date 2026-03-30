package org.feature.management.dashboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.models.DashboardStats;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/dashboard")
@Slf4j
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public Mono<ResponseEntity<DashboardStats>> getDashboardStats() {
        log.debug("GET /dashboard/stats - Fetching dashboard statistics");
        return dashboardService.getDashboardStats()
                .map(ResponseEntity::ok);
    }
}
