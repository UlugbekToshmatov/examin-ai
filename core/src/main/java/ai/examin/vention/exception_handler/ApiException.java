package ai.examin.vention.exception_handler;

import ai.examin.vention.enums.ResponseStatus;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ResponseStatus responseStatus;

    public ApiException(ResponseStatus responseStatus) {
        super(responseStatus.name());
        this.responseStatus = responseStatus;
    }
}
