package ai.examin.admin.model.clients.auth.error_handler;

import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.exception_handler.ApiException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.debug("Decoding error for method: {}, status: {}", methodKey, response.status());

        switch (response.status()) {
            case 404:
                if (
                    methodKey.contains("getUserById") || methodKey.contains("updateAccount")
                    || methodKey.contains("updateRole") || methodKey.contains("deleteAccount")
                ) {
                    log.error("User not found in auth-service for method: {}", methodKey);
                    return new ApiException(ResponseStatus.USER_NOT_FOUND);
                }
                return new ApiException(ResponseStatus.RESOURCE_NOT_FOUND);
            case 400:
                if (
                    methodKey.contains("getUserById") || methodKey.contains("updateAccount") || methodKey.contains("createAccount")
                    || methodKey.contains("updateRole") || methodKey.contains("deleteAccount")
                ) {
                    log.error("Bad request in auth-service for method: {}", methodKey);
                    return new ApiException(ResponseStatus.INVALID_REQUEST);
                }
                return new ApiException(ResponseStatus.INVALID_REQUEST);

            case 401:
                log.error("Unauthorized access to auth-service");
                return new ApiException(ResponseStatus.UNAUTHORIZED);

            case 403:
                log.error("Forbidden access to auth-service");
                return new ApiException(ResponseStatus.FORBIDDEN);

            case 500:
                log.error("Internal server error in auth-service");
                return new ApiException(ResponseStatus.INTERNAL_SERVER_ERROR);

            case 503:
                log.error("Auth-service is unavailable");
                return new ApiException(ResponseStatus.INTERNAL_SERVER_ERROR);

            default:
                log.error("Unexpected error from auth-service: {}", response.status());
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
