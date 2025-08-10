package ai.examin.core.exception_handler;

import ai.examin.core.base_classes.HttpResponse;
import ai.examin.core.enums.ResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<HttpResponse> handleApiException(ApiException e) {
        log.error(e.getResponseStatus().name().concat(": ").concat(e.getResponseStatus().getDescription()));

        return ResponseEntity
            .status(e.getResponseStatus().getHttpStatus())
            .body(
                HttpResponse.builder()
                    .statusCode(e.getResponseStatus().getStatusCode())
                    .description(e.getResponseStatus().getDescription())
                    .responseStatus(e.getResponseStatus())
                    .build()
            );
    }

    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentValidationException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(
            fieldError -> validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        exception.getBindingResult().getGlobalErrors().forEach(
            objError -> validationErrors.put(objError.getObjectName(), objError.getDefaultMessage())
        );

        return validationErrors;
    }

    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HttpResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.error(exception.getCause().getMessage());

        return ResponseEntity
            .status(ResponseStatus.INVALID_REQUEST.getHttpStatus())
            .body(
                HttpResponse.builder()
                    .statusCode(ResponseStatus.INVALID_REQUEST.getStatusCode())
                    .description(exception.getCause().getMessage())
                    .responseStatus(ResponseStatus.INVALID_REQUEST)
                    .build()
            );
    }

    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.error(exception.getMessage());

        return ResponseEntity
            .status(ResponseStatus.INVALID_REQUEST.getHttpStatus())
            .body(
                HttpResponse.builder()
                    .statusCode(ResponseStatus.INVALID_REQUEST.getStatusCode())
                    .description(exception.getMessage())
                    .responseStatus(ResponseStatus.INVALID_REQUEST)
                    .build()
            );
    }

    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<HttpResponse> handleNoResourceFoundException(NoResourceFoundException exception) {
        log.error(exception.getMessage());

        return ResponseEntity
            .status(ResponseStatus.INVALID_REQUEST.getHttpStatus())
            .body(
                HttpResponse.builder()
                    .statusCode(ResponseStatus.INVALID_REQUEST.getStatusCode())
                    .description(exception.getMessage())
                    .responseStatus(ResponseStatus.INVALID_REQUEST)
                    .build()
            );
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<HttpResponse> handleInternalServerError(Throwable e) {
        log.error("Unknown error:", e);
        ResponseStatus responseStatus;
        if (e instanceof MissingServletRequestParameterException) {
            responseStatus = ResponseStatus.REQUEST_PARAMETER_NOT_FOUND;
        } else
            responseStatus = ResponseStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity
            .status(responseStatus.getHttpStatus())
            .body(
                HttpResponse.builder()
                    .statusCode(responseStatus.getStatusCode())
                    .description(responseStatus.getDescription())
                    .responseStatus(responseStatus)
                    .build()
            );
    }
}
