package org.feature.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.models.Environment;
import org.feature.management.models.EnvironmentRequest;
import org.feature.management.models.EnvironmentResponse;
import org.feature.management.service.EnvironmentServiceInterface;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/environments")
@RequiredArgsConstructor
public class EnvironmentController {
    private final EnvironmentServiceInterface environmentService;

    @PostMapping
    public ResponseEntity<UUID> createEnvironment(@RequestBody @Valid EnvironmentRequest env) {
        log.debug("Creating environment inside controller {}", env);
        UUID environmentId = environmentService.createEnvironment(env);
        log.info("environment {} created", env.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(environmentId);
    }

    @GetMapping("/{id}")
    public Environment getEnvironmentById(@PathVariable UUID id) {
        log.debug("Getting environment inside controller by id {}", id);
        return environmentService.getById(id);
    }

    @GetMapping
    public EnvironmentResponse getAllEnvironments(@RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam(value = "size", defaultValue = "25") Integer size, @RequestParam(value = "sort", required = false) String sort) {
        log.debug("Getting all environments inside controller");
        Page<Environment> environmentsPage = environmentService.getAllEnvironments(page, size, sort);
        return EnvironmentResponse.builder().totalPages(environmentsPage.getTotalPages()).totalItems((int) environmentsPage.getTotalElements()).page(page).size(size).items(environmentsPage.getContent()).build();
    }

    @PostMapping("/{envId}/owners/{owner}")
    public ResponseEntity<?> addOwnerToEnvironment(@PathVariable UUID envId, @PathVariable String owner) {
        log.debug("inside controller Adding owner {} to environment {}", owner, envId);
        environmentService.assignOwnerToEnvironment(envId, owner);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{envId}/owners/{owner}")
    public ResponseEntity<?> removeOwnerFromEnvironment(@PathVariable UUID envId, @PathVariable String owner) {
        log.debug("inside controller Removing owner {} from environment {}", owner, envId);
        environmentService.removeOwnerFromEnvironment(envId, owner);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateEnvironment(@PathVariable UUID id, @RequestBody @Valid EnvironmentRequest request) {
        log.debug("inside controller Updating environment {}", request);
        environmentService.updateEnvironment(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEnvironment(@PathVariable UUID id) {
        log.debug("inside controller Deleting environment {}", id);
        environmentService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
