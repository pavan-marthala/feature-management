package org.feature.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.models.Environment;
import org.feature.management.models.EnvironmentRequest;
import org.feature.management.models.EnvironmentsGet200Response;
import org.feature.management.models.EnvironmentsIdPatchRequest;
import org.feature.management.service.EnvironmentService;
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
    private final EnvironmentService environmentService;

    @PostMapping
    public ResponseEntity<UUID> createEnvironment(@RequestBody @Valid EnvironmentRequest env) {
        log.info("inside controller Creating environment {}", env);
        return ResponseEntity.status(HttpStatus.CREATED).body(environmentService.createEnvironment(env));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Environment> getEnvironmentById(@PathVariable UUID id) {
        log.info("inside controller Getting environment by id {}", id);
        return ResponseEntity.ok(environmentService.getById(id));
    }

    @GetMapping
    public EnvironmentsGet200Response getAllEnvironments(@RequestParam(value = "page",defaultValue = "0") Integer page,@RequestParam(value = "size",defaultValue = "40") Integer size) {
        log.info("inside controller Getting all environments");
        Page<Environment> environmentsPage = environmentService.getAllEnvironments(page, size);
        return EnvironmentsGet200Response.builder().totalPages(environmentsPage.getTotalPages()).totalItems((int)environmentsPage.getTotalElements()).page(page).size(size).items(environmentsPage.getContent()).build();
    }

    @PostMapping("/{envId}/owners/{owner}")
    public ResponseEntity<?>  addOwnerToEnvironment(@PathVariable UUID envId, @PathVariable String  owner) {
        log.info("inside controller Adding owner {} to environment {}", owner, envId);
        environmentService.assignOwnerToEnvironment(envId, owner);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{envId}/owners/{owner}")
    public ResponseEntity<?> removeOwnerFromEnvironment(@PathVariable UUID envId, @PathVariable String owner, @RequestHeader("If-Match") Long ifMatch) {
        log.info("inside controller Removing owner {} from environment {}", owner, envId);
        environmentService.removeOwnerFromEnvironment(envId, owner,ifMatch);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateEnvironment(@PathVariable UUID id,@RequestHeader("If-Match") Long ifMatch, @RequestBody @Valid EnvironmentsIdPatchRequest request) {
        log.info("inside controller Updating environment {}", request);
        environmentService.updateEnvironment(id, ifMatch, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEnvironment(@PathVariable UUID id, @RequestHeader("If-Match") Long ifMatch) {
        log.info("inside controller Deleting environment {}", id);
        environmentService.deleteById(id, ifMatch);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
