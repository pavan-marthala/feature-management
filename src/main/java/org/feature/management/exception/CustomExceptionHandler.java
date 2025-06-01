package org.feature.management.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.models.Error;
import org.feature.management.models.ErrorDetailedErrorsInner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation errors occurred for request {} {}: {}",
                request.getMethod(), request.getRequestURI(), ex.getBindingResult());
        List<ErrorDetailedErrorsInner> detailedErrors = getErrorDetailedErrorsInners(ex);
        Error error = createBaseError(request, HttpStatus.BAD_REQUEST);
        error.setErrorMessage("Validation failed for request. See detailedErrors for more information.");
        error.setDetailedErrors(detailedErrors);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Error> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        log.error("Resource not found for request {} {}: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        Error error = createBaseError(request, HttpStatus.NOT_FOUND);
        error.setErrorMessage(ex.getMessage());
        error.setDetailedErrors(null);

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Error> handleIOException(IOException ex, HttpServletRequest request) {
        log.error("IO error occurred for request {} {}: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        Error error = createBaseError(request, HttpStatus.INTERNAL_SERVER_ERROR);
        error.setErrorMessage("An IO error occurred while processing the request");
        error.setDetailedErrors(null);

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred for request {} {}: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        Error error = createBaseError(request, HttpStatus.INTERNAL_SERVER_ERROR);
        error.setErrorMessage("An unexpected error occurred while processing the request");
        error.setDetailedErrors(null);

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private List<ErrorDetailedErrorsInner> getErrorDetailedErrorsInners(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    ErrorDetailedErrorsInner errorDetail = new ErrorDetailedErrorsInner();
                    errorDetail.setField(fieldError.getField());
                    errorDetail.setValue(fieldError.getRejectedValue() != null ?
                            fieldError.getRejectedValue().toString() : "null");
                    errorDetail.setMessage(fieldError.getDefaultMessage());
                    errorDetail.setErrorCode(fieldError.getCode());
                    return errorDetail;
                })
                .toList();
    }


    private Error createBaseError(HttpServletRequest request, HttpStatus status) {
        Error error = new Error();
        error.setCorrelationIdentifier(UUID.randomUUID());
        error.setErrorTimestamp(OffsetDateTime.now());
        error.statusCode(BigDecimal.valueOf(status.value()));
        error.setHttpMethod(Error.HttpMethodEnum.valueOf(request.getMethod()));
        error.setRequestUri(request.getRequestURI());
        return error;
    }
}