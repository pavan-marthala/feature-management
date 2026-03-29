package org.feature.management.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.feature.management.models.ErrorDetails;
import org.feature.management.models.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Error> handleWebFluxValidationException(WebExchangeBindException ex,
            ServerWebExchange exchange) {
        log.error("Validation errors occurred for request {} {}: {}",
                exchange.getRequest().getMethod(), exchange.getRequest().getURI().getPath(), ex.getBindingResult());
        List<ErrorDetails> detailedErrors = getErrorDetailedErrorsInners(ex);
        Error error = createBaseError(exchange, HttpStatus.BAD_REQUEST);
        error.setErrorMessage("Validation failed for request. See detailedErrors for more information.");
        error.setDetailedErrors(detailedErrors);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Error> handleResourceNotFoundException(ResourceNotFoundException ex,
            ServerWebExchange exchange) {
        log.error("Resource not found for request {} {}: {}",
                exchange.getRequest().getMethod().name(), exchange.getRequest().getURI().getPath(), ex.getMessage());

        Error error = createBaseError(exchange, HttpStatus.NOT_FOUND);
        error.setErrorMessage(ex.getMessage());
        error.setDetailedErrors(null);

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeatureException.class)
    public ResponseEntity<Error> handleFeatureException(FeatureException ex, ServerWebExchange exchange) {
        log.error("Feature exception occurred for request {} {}: {}",
                exchange.getRequest().getMethod().name(), exchange.getRequest().getURI().getPath(), ex.getMessage());

        Error error = createBaseError(exchange, HttpStatus.NOT_FOUND);
        error.setErrorMessage(ex.getMessage());
        error.setDetailedErrors(null);

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EnvironmentException.class)
    public ResponseEntity<Error> handleEnvironmentException(EnvironmentException ex, ServerWebExchange exchange) {
        log.error("Environment exception occurred for request {} {}: {}",
                exchange.getRequest().getMethod().name(), exchange.getRequest().getURI().getPath(), ex.getMessage());

        Error error = createBaseError(exchange, HttpStatus.NOT_FOUND);
        error.setErrorMessage(ex.getMessage());
        error.setDetailedErrors(null);

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Error> handleIOException(IOException ex, ServerWebExchange exchange) {
        log.error("IO error occurred for request {} {}: {}",
                exchange.getRequest().getMethod().name(), exchange.getRequest().getURI().getPath(), ex.getMessage());

        Error error = createBaseError(exchange, HttpStatus.INTERNAL_SERVER_ERROR);
        error.setErrorMessage(ex.getMessage());
        error.setDetailedErrors(null);

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGeneralException(Exception ex, ServerWebExchange exchange) {
        log.error("Unexpected error occurred for request {} {}: {} stack trace: {}",
                exchange.getRequest().getMethod().name(), exchange.getRequest().getURI().getPath(), ex.getMessage(),
                ex.fillInStackTrace());

        Error error = createBaseError(exchange, HttpStatus.INTERNAL_SERVER_ERROR);
        error.setErrorMessage(ex.getMessage());
        error.setDetailedErrors(null);

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private List<ErrorDetails> getErrorDetailedErrorsInners(WebExchangeBindException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    ErrorDetails errorDetail = new ErrorDetails();
                    errorDetail.setField(fieldError.getField());
                    errorDetail.setValue(
                            fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : "null");
                    errorDetail.setMessage(fieldError.getDefaultMessage());
                    errorDetail.setErrorCode(fieldError.getCode());
                    return errorDetail;
                })
                .toList();
    }

    private Error createBaseError(ServerWebExchange exchange, HttpStatus status) {
        Error error = new Error();
        error.setCorrelationIdentifier(UUID.randomUUID());
        error.setErrorTimestamp(OffsetDateTime.now());
        error.statusCode(BigDecimal.valueOf(status.value()));
        error.setHttpMethod(Error.HttpMethodEnum.valueOf(exchange.getRequest().getMethod().name()));
        error.setRequestUri(exchange.getRequest().getURI().getPath());
        return error;
    }
}
