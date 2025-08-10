package ai.examin.core.exception_handler;

import ai.examin.core.base_classes.HttpResponse;
import ai.examin.core.enums.ResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

@RequiredArgsConstructor
public class AuthExceptionHandler {

    public static void handleAuthorizationFailure(HttpServletResponse response, ObjectMapper objectMapper) throws IOException {
        HttpResponse httpResponse = HttpResponse.builder()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .description(HttpStatus.UNAUTHORIZED.name())
            .data("Authentication failed or token expired")
            .build();

        writeResponse(response, httpResponse, HttpStatus.UNAUTHORIZED, objectMapper);
    }

    public static void handleAuthenticationFailure(HttpServletResponse response, ObjectMapper objectMapper) throws IOException {
        HttpResponse httpResponse = HttpResponse.builder()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .description(HttpStatus.FORBIDDEN.name())
            .data("You do not have enough permissions to access this resource")
            .build();

        writeResponse(response, httpResponse, HttpStatus.FORBIDDEN, objectMapper);
    }

    public static void handleAuthFailure(HttpServletResponse response, ApiException exception, ObjectMapper objectMapper) throws IOException {
        HttpResponse httpResponse = getHttpResponse(exception.getResponseStatus());
        writeResponse(response, httpResponse, objectMapper);
    }

    private static HttpResponse getHttpResponse(ResponseStatus responseStatus) {
        return HttpResponse.builder()
            .statusCode(responseStatus.getStatusCode())
            .description(responseStatus.getDescription())
            .data(responseStatus)
            .build();
    }

    private static void writeResponse(HttpServletResponse response, HttpResponse httpResponse, ObjectMapper objectMapper) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ServletOutputStream outputStream = response.getOutputStream();
        objectMapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
        outputStream.close();
    }

    private static void writeResponse(HttpServletResponse response, HttpResponse httpResponse, HttpStatus httpStatus, ObjectMapper objectMapper) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        ServletOutputStream outputStream = response.getOutputStream();
        objectMapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
        outputStream.close();
    }

}
