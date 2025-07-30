package ai.examin.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseStatus {
    OK(0, HttpStatus.OK, "Success"),
    INTERNAL_SERVER_ERROR(1, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    REQUEST_PARAMETER_NOT_FOUND(2, HttpStatus.BAD_REQUEST, "Request parameter not found"),
    USER_NOT_FOUND(100, HttpStatus.NOT_FOUND, "User not found"),
    EMAIL_ALREADY_REGISTERED(101, HttpStatus.NOT_ACCEPTABLE, "This email is already registered");


    private final Integer statusCode;
    private final HttpStatus httpStatus;
    private final String description;
}
