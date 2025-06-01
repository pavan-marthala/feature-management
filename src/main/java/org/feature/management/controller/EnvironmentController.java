package org.feature.management.controller;

import lombok.extern.slf4j.Slf4j;
import org.feature.management.service.EnvironmentService;
import org.feature.management.service.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/environments")
public class EnvironmentController {
    @Autowired
    private EnvironmentService environmentService;

    @PutMapping("/{envId}/owners/{ownerId}")
    public ResponseEntity<?> addOwnerToEnvironment(@PathVariable Long envId, @PathVariable Long ownerId) {
        log.info("inside controller Adding owner {} to environment {}", ownerId, envId);
        return ResponseEntity.ok(environmentService.assignOwnerToEnvironment(envId, ownerId));
    }

    @DeleteMapping("/{envId}/owners/{ownerId}")
    public ResponseEntity<?> removeOwnerFromEnvironment(@PathVariable Long envId, @PathVariable Long ownerId) {
        log.info("inside controller Removing owner {} from environment {}", ownerId, envId);
        return ResponseEntity.ok(environmentService.removeOwnerFromEnvironment(envId, ownerId));
    }
}
