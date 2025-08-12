package ai.examin.notification.model.clients.auth;

import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.exception_handler.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthClientFallbackFactory implements FallbackFactory<AuthClient> {
    @Override
    public AuthClient create(Throwable cause) {
        return email -> {
            log.error("Failed to get user by email: {}", email, cause);
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);
        };
    }
}
