package org.feature.management.controller;

import lombok.extern.slf4j.Slf4j;
import org.feature.management.service.EnvironmentService;
import org.feature.management.service.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/environments")
public class EnvironmentController {
    @Autowired
    private EnvironmentService environmentService;

    @PutMapping("/{envId}/owners/{owner}")
    public ResponseEntity<?> addOwnerToEnvironment(@PathVariable UUID envId, @PathVariable String  owner) {
        log.info("inside controller Adding owner {} to environment {}", owner, envId);
        return ResponseEntity.ok(environmentService.assignOwnerToEnvironment(envId, owner));
    }

    @DeleteMapping("/{envId}/owners/{owner}")
    public ResponseEntity<?> removeOwnerFromEnvironment(@PathVariable UUID envId, @PathVariable String owner) {
        log.info("inside controller Removing owner {} from environment {}", owner, envId);
        return ResponseEntity.ok(environmentService.removeOwnerFromEnvironment(envId, owner));
    }
}
