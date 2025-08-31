package ai.examin.admin.model.clients.auth;

import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.core.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthClientFallbackFactory implements FallbackFactory<AuthClient> {
    @Override
    public AuthClient create(Throwable cause) {
        return new AuthClient() {

            @Override
            public UserPrincipal getUserByEmail(String email) {
                log.error("Failed to get user by email: {}. Cause: {}", email, cause.getMessage());
                // Return null object as a fallback
                return null;
            }

            @Override
            public UserResponse getUserById(Long id) {
                log.error("Failed to get user by id: {}. Cause: {}", id, cause.getMessage());
                // Return null object as a fallback
                return null;
            }

            @Override
            public UserResponse getUserByIdInternal(UUID id) {
                log.error("Failed to get user by id: {}. Cause: {}", id, cause.getMessage());
                return null;
            }

            @Override
            public UserResponse getUserByExternalId(String externalId) {
                log.error("Failed to get user by external ID: {}. Cause: {}", externalId, cause.getMessage());
                return null;
            }
        };
    }
}
