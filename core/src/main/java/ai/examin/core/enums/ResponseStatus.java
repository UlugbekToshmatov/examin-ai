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
    INVALID_REQUEST(3, HttpStatus.BAD_REQUEST, "Invalid request"),
    TOKEN_REQUIRED(4, HttpStatus.BAD_REQUEST, "Missing JWT token"),
    TOKEN_NOT_FOUND(5, HttpStatus.BAD_REQUEST, "Token not found"),
    INVALID_TOKEN(6, HttpStatus.BAD_REQUEST, "Invalid token"),
    TOKEN_EXPIRED(7, HttpStatus.BAD_REQUEST, "Token expired"),
    TOKEN_MISMATCH_EXCEPTION(8, HttpStatus.BAD_REQUEST, "This token does not belong to you! Use your own token."),
    TOKEN_PROCESSING_ERROR(9, HttpStatus.INTERNAL_SERVER_ERROR, "Token processing error"),
    TOKEN_MUST_START_WITH_BEARER(10, HttpStatus.BAD_REQUEST, "Token must start with 'Bearer '"),
    UNKNOWN_TYPE(11, HttpStatus.BAD_REQUEST, "Unknown type provided"),
    ERROR_GETTING_ACCESS_TOKEN(12, HttpStatus.INTERNAL_SERVER_ERROR, "Error while getting access token"),
    METHOD_NOT_ALLOWED(13, HttpStatus.BAD_REQUEST, "Method not allowed"),
    USER_NOT_FOUND(100, HttpStatus.NOT_FOUND, "User not found"),
    EMAIL_ALREADY_REGISTERED(101, HttpStatus.BAD_REQUEST, "This email is already registered"),
    USERNAME_ALREADY_REGISTERED(102, HttpStatus.BAD_REQUEST, "This username is already registered"),
    EMAIL_OR_USERNAME_ALREADY_REGISTERED(103, HttpStatus.BAD_REQUEST, "This email or username is already registered"),
    EMAIL_OR_USERNAME_NOT_REGISTERED(104, HttpStatus.BAD_REQUEST, "This email or username is not registered in the system"),
    EMAIL_REQUIRED(105, HttpStatus.BAD_REQUEST, "Email is required"),
    EMAIL_CONFIRMATION_REQUIRED(106, HttpStatus.BAD_REQUEST, "Please, confirm your email address registered in the system"),
    INCORRECT_PASSWORD(107, HttpStatus.BAD_REQUEST, "Incorrect password"),
    PASSWORDS_DO_NOT_MATCH(108, HttpStatus.BAD_REQUEST, "New password and confirm password do not match"),
    PROVIDE_NEW_PASSWORD(109, HttpStatus.BAD_REQUEST, "Provide different password than your old password"),
    ERROR_REGISTERING_USER(110, HttpStatus.INTERNAL_SERVER_ERROR, "Error while registering user"),
    LISTENER_CONTAINER_NOT_FOUND(200, HttpStatus.NOT_FOUND, "Listener container not found"),
    LISTENER_CONTAINER_ALREADY_STARTED(201, HttpStatus.BAD_REQUEST, "Listener container already started"),
    LISTENER_CONTAINER_ALREADY_STOPPED(202, HttpStatus.BAD_REQUEST, "Listener container already stopped"),
    COURSE_NOT_FOUND(300, HttpStatus.NOT_FOUND, "Course not found"),
    COURSE_ALREADY_EXISTS(301, HttpStatus.BAD_REQUEST, "Course with this name already exists"),
    PROGRAM_NOT_FOUND(400, HttpStatus.NOT_FOUND, "Program not found"),
    EXPERT_ID_REQUIRED(401, HttpStatus.BAD_REQUEST, "Expert ID is required");


    private final Integer statusCode;
    private final HttpStatus httpStatus;
    private final String description;
}
