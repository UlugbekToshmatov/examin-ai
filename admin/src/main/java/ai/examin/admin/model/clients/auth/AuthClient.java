package ai.examin.admin.model.clients.auth;

import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.core.config.FeignClientConfig;
import ai.examin.core.security.UserPrincipal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
    name = "auth-service",
    configuration = FeignClientConfig.class,
    fallbackFactory = AuthClientFallbackFactory.class
)
public interface AuthClient {
    @GetMapping("/api/v1/user/internal/email/{email}")
    UserPrincipal getUserByEmail(@PathVariable String email);

    @GetMapping("/api/v1/user/internal/id/{id}")
    UserResponse getUserById(@PathVariable Long id);

    @GetMapping("/api/v1/user/internal/id/{id}")
    UserResponse getUserByIdInternal(@PathVariable UUID id);

    @GetMapping("/api/v1/user/internal/external-id/{external-id}")
    UserResponse getUserByExternalId(@PathVariable("external-id") String externalId);
}
