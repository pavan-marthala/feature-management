package org.feature.management.shared.filters;

import org.feature.management.feature.FeatureEntity;
import org.feature.management.feature.FeatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeatureETagValidationFilterTest {

    @Mock
    private FeatureRepository featureRepository;

    @Mock
    private WebFilterChain filterChain;

    private FeatureETagValidationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new FeatureETagValidationFilter(featureRepository);
        filter.init();
    }

    @Test
    void shouldPassWhenNotETagMethod() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/features/" + UUID.randomUUID()).build());
        
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
    }

    @Test
    void shouldReturnPreconditionRequiredWhenHeaderMissing() {
        UUID id = UUID.randomUUID();
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.patch("/features/" + id).build());

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_REQUIRED);
        verifyNoInteractions(filterChain);
    }

    @Test
    void shouldReturnPreconditionFailedWhenETagMismatch() {
        UUID id = UUID.randomUUID();
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.patch("/features/" + id)
                        .header("If-Match", "1")
                        .build());

        FeatureEntity entity = new FeatureEntity();
        entity.setEtag(2L);

        when(featureRepository.findById(id)).thenReturn(Mono.just(entity));

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
        verifyNoInteractions(filterChain);
    }

    @Test
    void shouldPassWhenETagMatches() {
        UUID id = UUID.randomUUID();
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.patch("/features/" + id)
                        .header("If-Match", "2")
                        .build());

        FeatureEntity entity = new FeatureEntity();
        entity.setEtag(2L);

        when(featureRepository.findById(id)).thenReturn(Mono.just(entity));
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
    }
    @Test
    void shouldReturnBadRequestWhenIfMatchHeaderIsInvalid() {
        UUID id = UUID.randomUUID();
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.patch("/features/" + id)
                        .header("If-Match", "not-a-number")
                        .build());

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldPassWhenEntityNotFound() {
        UUID id = UUID.randomUUID();
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.patch("/features/" + id)
                        .header("If-Match", "1")
                        .build());

        when(featureRepository.findById(id)).thenReturn(Mono.empty());
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
    }

    @Test
    void shouldPassWhenPathDoesNotMatchAnyRoute() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.patch("/invalid-path")
                        .header("If-Match", "1")
                        .build());

        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
    }
}
