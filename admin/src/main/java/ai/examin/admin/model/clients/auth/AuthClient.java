package ai.examin.admin.model.clients.auth;

import ai.examin.admin.model.clients.auth.dto.AccountCreationRequest;
import ai.examin.admin.model.profile.dto.UserUpdateRequest;
import ai.examin.core.base_classes.HttpResponse;
import ai.examin.core.config.FeignClientConfig;
import ai.examin.core.enums.Role;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
    name = "auth-service",
    configuration = FeignClientConfig.class,
    fallbackFactory = AuthClientFallbackFactory.class
)
public interface AuthClient {

    @GetMapping("/api/v1/user")
    HttpResponse getUserById(@RequestParam("id") UUID id);

    @PostMapping("/api/v1/user/create")
    HttpResponse createAccount(@RequestBody @Valid AccountCreationRequest request);

    @PutMapping("/api/v1/user")
    HttpResponse updateAccount(@RequestParam("id") UUID id, @RequestBody @Valid UserUpdateRequest request);

    @PutMapping("/api/v1/user/role")
    HttpResponse updateRole(@RequestParam("id") UUID id, @RequestParam Role role);

    @DeleteMapping("/api/v1/user")
    HttpResponse deleteAccount(@RequestParam("id") UUID id);

}
