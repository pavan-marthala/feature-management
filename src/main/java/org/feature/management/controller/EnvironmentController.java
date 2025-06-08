package org.feature.management.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.entity.EnvironmentEntity;
import org.feature.management.models.Environment;
import org.feature.management.service.EnvironmentService;
import org.feature.management.service.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/environments")
public class EnvironmentController {
    @Autowired
    private EnvironmentService environmentService;

    @PostMapping
    public ResponseEntity<?> createEnvironment(@RequestBody @Valid Environment env) {
        log.info("inside controller Creating environment {}", env);
        EnvironmentEntity createdEnv = environmentService.createEnvironment(env);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<EnvironmentEntity> getAllEnvironments() {
        return List.of();
    }

    @PutMapping("/{envId}/owners/{owner}")
    public EnvironmentEntity addOwnerToEnvironment(@PathVariable UUID envId, @PathVariable String  owner) {
        log.info("inside controller Adding owner {} to environment {}", owner, envId);
        return environmentService.assignOwnerToEnvironment(envId, owner);
    }

    @DeleteMapping("/{envId}/owners/{owner}")
    public ResponseEntity<?> removeOwnerFromEnvironment(@PathVariable UUID envId, @PathVariable String owner) {
        log.info("inside controller Removing owner {} from environment {}", owner, envId);
        return ResponseEntity.ok(environmentService.removeOwnerFromEnvironment(envId, owner));
    }
}
