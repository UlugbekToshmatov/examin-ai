package ai.examin.admin.model.clients.auth;

import ai.examin.admin.model.clients.auth.config.AdminServiceInternalFeignClientConfig;
import ai.examin.admin.model.clients.auth.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
    name = "auth-service-internal",
    url = "${feign.client.auth-service.url}",
    configuration = AdminServiceInternalFeignClientConfig.class,
    fallbackFactory = AuthServiceClientFallbackFactory.class
)
public interface AuthServiceClient {

    @GetMapping("/api/v1/user/internal/id/{id}")
    UserResponse getUserById(@PathVariable UUID id);

}
