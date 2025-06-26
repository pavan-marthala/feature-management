package org.feature.management.filters;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.feature.management.interfaces.ETaggableEntity;
import org.feature.management.record.ETagRoute;
import org.feature.management.repository.EnvironmentRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class EnvironmentETagValidationFilter extends OncePerRequestFilter {

    private final EnvironmentRepository environmentRepo;
    private final List<ETagRoute> routes = new ArrayList<>();
    private static final Set<String> ETAG_METHODS = Set.of("PATCH", "DELETE");

    @PostConstruct
    public void init() {
        routes.add(new ETagRoute(
                Pattern.compile("^/environments/([0-9a-fA-F\\-]+)$"),
                matcher -> UUID.fromString(matcher.group(1)),
                environmentRepo::findById
        ));
        routes.add(new ETagRoute(
                Pattern.compile("^/environments/([0-9a-fA-F\\-]+)/owners/[^/]+$"),
                matcher -> UUID.fromString(matcher.group(1)),
                environmentRepo::findById
        ));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!ETAG_METHODS.contains(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String ifMatchHeader = request.getHeader("If-Match");
        if (ifMatchHeader == null) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Missing If-Match header");
            return;
        }

        Optional<ETagRoute> matchingRoute = routes.stream().filter(route -> route.pattern().matcher(path).matches()).findFirst();

        if (matchingRoute.isPresent()) {
            if (!validateETag(matchingRoute.get(), path, ifMatchHeader, response)) {
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean validateETag(ETagRoute route, String path, String ifMatchHeader, HttpServletResponse response) throws IOException {
        try {
            Matcher matcher = route.pattern().matcher(path);
            if (!matcher.matches()) {
                return true;
            }

            UUID id = route.idExtractor().apply(matcher);
            Long ifMatch = Long.parseLong(ifMatchHeader);

            Optional<? extends ETaggableEntity> entityOpt = route.entityFetcher().apply(id);
            if (entityOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found with id: " + id);
                return false;
            }

            Long actualEtag = entityOpt.get().getEtag();
            if (!Objects.equals(actualEtag, ifMatch)) {
                response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "ETag does not match");
                return false;
            }

            return true;

        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid UUID or If-Match format");
            return false;
        }

    }
}
