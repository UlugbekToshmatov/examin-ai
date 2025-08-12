package ai.examin.notification.model.clients.auth;

import ai.examin.core.config.FeignClientConfig;
import ai.examin.core.security.UserPrincipal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "auth-service",
    configuration = FeignClientConfig.class,
    fallbackFactory = AuthClientFallbackFactory.class
)
public interface AuthClient {
    @GetMapping("/api/v1/user/internal/email/{email}")
    UserPrincipal getUserByEmail(@PathVariable String email);
}
