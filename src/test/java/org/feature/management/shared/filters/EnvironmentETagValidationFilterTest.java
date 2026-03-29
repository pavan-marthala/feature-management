package org.feature.management.shared.filters;

import org.feature.management.environment.EnvironmentEntity;
import org.feature.management.environment.EnvironmentRepository;
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
class EnvironmentETagValidationFilterTest {

        @Mock
        private EnvironmentRepository environmentRepository;

        @Mock
        private WebFilterChain filterChain;

        private EnvironmentETagValidationFilter filter;

        @BeforeEach
        void setUp() {
                filter = new EnvironmentETagValidationFilter(environmentRepository);
                filter.init();
        }

        @Test
        void shouldPassWhenNotETagMethod() {
                MockServerWebExchange exchange = MockServerWebExchange.from(
                                MockServerHttpRequest.get("/environments/" + UUID.randomUUID()).build());

                when(filterChain.filter(exchange)).thenReturn(Mono.empty());

                StepVerifier.create(filter.filter(exchange, filterChain))
                                .verifyComplete();

                verify(filterChain).filter(exchange);
        }

        @Test
        void shouldReturnPreconditionRequiredWhenHeaderMissing() {
                UUID id = UUID.randomUUID();
                MockServerWebExchange exchange = MockServerWebExchange.from(
                                MockServerHttpRequest.patch("/environments/" + id).build());

                StepVerifier.create(filter.filter(exchange, filterChain))
                                .verifyComplete();

                assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_REQUIRED);
                verifyNoInteractions(filterChain);
        }

        @Test
        void shouldReturnPreconditionFailedWhenETagMismatch() {
                UUID id = UUID.randomUUID();
                MockServerWebExchange exchange = MockServerWebExchange.from(
                                MockServerHttpRequest.patch("/environments/" + id)
                                                .header("If-Match", "1")
                                                .build());

                EnvironmentEntity entity = new EnvironmentEntity();
                entity.setEtag(2L);

                when(environmentRepository.findById(id)).thenReturn(Mono.just(entity));

                StepVerifier.create(filter.filter(exchange, filterChain))
                                .verifyComplete();

                assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
                verifyNoInteractions(filterChain);
        }

        @Test
        void shouldPassWhenETagMatches() {
                UUID id = UUID.randomUUID();
                MockServerWebExchange exchange = MockServerWebExchange.from(
                                MockServerHttpRequest.patch("/environments/" + id)
                                                .header("If-Match", "2")
                                                .build());

                EnvironmentEntity entity = new EnvironmentEntity();
                entity.setEtag(2L);

                when(environmentRepository.findById(id)).thenReturn(Mono.just(entity));
                when(filterChain.filter(exchange)).thenReturn(Mono.empty());

                StepVerifier.create(filter.filter(exchange, filterChain))
                                .verifyComplete();

                verify(filterChain).filter(exchange);
        }
    @Test
    void shouldReturnBadRequestWhenIfMatchHeaderIsInvalid() {
        UUID id = UUID.randomUUID();
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.patch("/environments/" + id)
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
                MockServerHttpRequest.patch("/environments/" + id)
                        .header("If-Match", "1")
                        .build());

        when(environmentRepository.findById(id)).thenReturn(Mono.empty());
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
