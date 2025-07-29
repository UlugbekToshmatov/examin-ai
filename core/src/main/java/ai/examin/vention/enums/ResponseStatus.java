package ai.examin.vention.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseStatus {
    OK(0, HttpStatus.OK, "Success"),
    INTERNAL_SERVER_ERROR(1, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    REQUEST_PARAMETER_NOT_FOUND(2, HttpStatus.BAD_REQUEST, "Request parameter not found");


    private final Integer statusCode;
    private final HttpStatus httpStatus;
    private final String description;
}
