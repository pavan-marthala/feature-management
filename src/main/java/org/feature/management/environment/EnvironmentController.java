package org.feature.management.environment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.models.Environment;
import org.feature.management.models.EnvironmentRequest;
import org.feature.management.models.EnvironmentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/environments")
@RequiredArgsConstructor
public class EnvironmentController {

    private final EnvironmentServiceInterface environmentService;

    @PostMapping
    public Mono<ResponseEntity<UUID>> createEnvironment(@RequestBody @Valid EnvironmentRequest env) {
        log.debug("Creating environment inside controller {}", env);
        return environmentService.createEnvironment(env)
                .map(uuid -> {
                    log.info("environment {} created", env.getName());
                    return ResponseEntity.status(HttpStatus.CREATED).body(uuid);
                });
    }

    @GetMapping("/{id}")
    public Mono<Environment> getEnvironmentById(@PathVariable UUID id) {
        log.debug("Getting environment inside controller by id {}", id);
        return environmentService.getById(id);
    }

    @GetMapping
    public Mono<EnvironmentResponse> getAllEnvironments(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "25") Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        log.debug("Getting all environments inside controller");
        return environmentService.getAllEnvironments(page, size, sort)
                .map(environmentsPage -> EnvironmentResponse.builder()
                        .totalPages(environmentsPage.getTotalPages())
                        .totalItems((int) environmentsPage.getTotalElements())
                        .page(page)
                        .size(size)
                        .items(environmentsPage.getContent())
                        .build());
    }

    @PostMapping("/{envId}/owners/{owner}")
    public Mono<ResponseEntity<Void>> addOwnerToEnvironment(@PathVariable UUID envId, @PathVariable String owner) {
        log.debug("inside controller Adding owner {} to environment {}", owner, envId);
        return environmentService.assignOwnerToEnvironment(envId, owner)
                .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @DeleteMapping("/{envId}/owners/{owner}")
    public Mono<ResponseEntity<Void>> removeOwnerFromEnvironment(@PathVariable UUID envId, @PathVariable String owner) {
        log.debug("inside controller Removing owner {} from environment {}", owner, envId);
        return environmentService.removeOwnerFromEnvironment(envId, owner)
                .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateEnvironment(@PathVariable UUID id, @RequestBody @Valid EnvironmentRequest request) {
        log.debug("inside controller Updating environment {}", request);
        return environmentService.updateEnvironment(id, request)
                .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEnvironment(@PathVariable UUID id) {
        log.debug("inside controller Deleting environment {}", id);
        return environmentService.deleteById(id)
                .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }
}
