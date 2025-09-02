package ai.examin.admin.model.clients.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthServiceClientFallbackFactory implements FallbackFactory<AuthServiceClient> {
    @Override
    public AuthServiceClient create(Throwable cause) {
        return id -> {
            log.error("Failed to get user by id: {}. Cause: {}", id, cause.getMessage());
            return null;
        };
    }
}
