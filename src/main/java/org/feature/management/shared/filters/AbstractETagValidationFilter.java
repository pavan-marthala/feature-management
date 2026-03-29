package org.feature.management.shared.filters;

import lombok.extern.slf4j.Slf4j;
import org.feature.management.shared.exception.ResourceNotFoundException;
import org.feature.management.shared.record.ETagRoute;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.regex.Matcher;

@Slf4j
public abstract class AbstractETagValidationFilter implements WebFilter {

    private static final Set<HttpMethod> ETAG_METHODS = Set.of(HttpMethod.PATCH, HttpMethod.DELETE);
    protected final List<ETagRoute> routes = new ArrayList<>();

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();

        if (!ETAG_METHODS.contains(method)) {
            return chain.filter(exchange);
        }

        String ifMatchHeader = exchange.getRequest().getHeaders().getFirst("If-Match");
        if (ifMatchHeader == null) {
            return writeError(exchange, HttpStatus.PRECONDITION_REQUIRED, "{\"error\": \"If-Match header is required\"}");
        }

        for (ETagRoute route : routes) {
            if (route.pattern().matcher(path).matches()) {
                return validateETag(route, path, ifMatchHeader, exchange, chain);
            }
        }

        return chain.filter(exchange);
    }

    private Mono<Void> validateETag(ETagRoute route, String path, String ifMatchHeader, ServerWebExchange exchange, WebFilterChain chain) {
        try {
            Matcher matcher = route.pattern().matcher(path);
            if (!matcher.matches()) {
                return chain.filter(exchange);
            }

            UUID id = route.idExtractor().apply(matcher);
            Long ifMatch = Long.parseLong(ifMatchHeader);

            return route.entityFetcher().apply(id)
                    .flatMap(entity -> {
                        Long actualEtag = entity.getEtag();
                        if (Objects.equals(actualEtag, ifMatch)) {
                            return chain.filter(exchange).then(Mono.just(entity));
                        }
                        return writeError(exchange, HttpStatus.PRECONDITION_FAILED, "{\"error\": \"ETag does not match\"}")
                                .then(Mono.just(entity));
                    })
                    .switchIfEmpty(Mono.defer(() -> chain.filter(exchange).then(Mono.empty())))
                    .then();

        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID or If-Match format: {}", e.getMessage());
            return writeError(exchange, HttpStatus.BAD_REQUEST, "{\"error\": \"Invalid UUID or If-Match format\"}");
        }
    }

    protected Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String body) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(new IllegalStateException("Response already committed"));
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes())));
    }
}
