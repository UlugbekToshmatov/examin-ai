package ai.examin.admin.model.clients.auth;

import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.exception_handler.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class AuthServiceClientFallbackFactory implements FallbackFactory<AuthServiceClient> {
    @Override
    public AuthServiceClient create(Throwable cause) {
        log.error("Fallback triggered for AuthServiceClient. Cause: {}", cause.getMessage());

        return new AuthServiceClient() {
            @Override
            public UserResponse getUserById(UUID id) {
                // This fallback is mainly for network failures, circuit breaker, etc.
                log.error("Fallback: Error calling auth-service for user {}: {}", id, cause.getMessage());

                if (cause instanceof java.net.ConnectException ||
                    cause instanceof java.net.SocketTimeoutException ||
                    cause.getMessage().contains("Connection refused")) {
                    log.error("Network error calling auth-service for user {}: {}", id, cause.getMessage());
                    throw new ApiException(ResponseStatus.INTERNAL_SERVER_ERROR);
                }

                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }

                throw new ApiException(ResponseStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }
}
