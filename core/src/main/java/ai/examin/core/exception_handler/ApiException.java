package ai.examin.core.exception_handler;

import ai.examin.core.enums.ResponseStatus;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ResponseStatus responseStatus;

    public ApiException(ResponseStatus responseStatus) {
        super(responseStatus.name());
        this.responseStatus = responseStatus;
    }
}
