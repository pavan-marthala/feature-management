package org.feature.management.shared.exception;

import org.feature.management.models.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CustomExceptionHandlerTest {

    private CustomExceptionHandler handler;
    private ServerWebExchange exchange;
    private ServerHttpRequest request;

    @BeforeEach
    void setUp() {
        handler = new CustomExceptionHandler();
        exchange = Mockito.mock(ServerWebExchange.class);
        request = Mockito.mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(URI.create("http://localhost/test"));
    }

    @Test
    void handleWebFluxValidationException_ReturnsBadRequestWithErrorDetails() {
        WebExchangeBindException ex = Mockito.mock(WebExchangeBindException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "rejectedValue", false, null, null, "defaultMessage");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Error> response = handler.handleWebFluxValidationException(ex, exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetailedErrors()).hasSize(1);
        assertThat(response.getBody().getDetailedErrors().get(0).getField()).isEqualTo("field");
    }

    @Test
    void handleResourceNotFoundException_ReturnsNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        ResponseEntity<Error> response = handler.handleResourceNotFoundException(ex, exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Not found");
    }

    @Test
    void handleFeatureException_ReturnsNotFound() {
        FeatureException ex = new FeatureException("Feature error");
        ResponseEntity<Error> response = handler.handleFeatureException(ex, exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Feature error");
    }

    @Test
    void handleEnvironmentException_ReturnsNotFound() {
        EnvironmentException ex = new EnvironmentException("Env error");
        ResponseEntity<Error> response = handler.handleEnvironmentException(ex, exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Env error");
    }

    @Test
    void handleIOException_ReturnsInternalServerError() {
        IOException ex = new IOException("IO error");
        ResponseEntity<Error> response = handler.handleIOException(ex, exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getErrorMessage()).isEqualTo("IO error");
    }

    @Test
    void handleGeneralException_ReturnsInternalServerError() {
        Exception ex = new Exception("General error");
        ResponseEntity<Error> response = handler.handleGeneralException(ex, exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getErrorMessage()).isEqualTo("General error");
    }
}
