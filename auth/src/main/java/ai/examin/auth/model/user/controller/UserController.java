package ai.examin.auth.model.user.controller;

import ai.examin.auth.model.user.dto.AccountCreationRequest;
import ai.examin.auth.model.user.dto.UserResponse;
import ai.examin.auth.model.user.dto.UserUpdateRequest;
import ai.examin.auth.model.user.service.UserService;
import ai.examin.core.base_classes.HttpResponse;
import ai.examin.core.enums.Role;
import ai.examin.core.enums.Status;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping()
    @PreAuthorize("#id == authentication.principal.claims['sub'] or hasRole('SUPERVISOR')")
    public HttpEntity<HttpResponse> getById(@RequestParam("id") UUID id) {
        UserResponse user = userService.getUserById(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("user", user))
                .build()
        );
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public HttpEntity<HttpResponse> createAccount(@RequestBody @Valid AccountCreationRequest request) {
        UserResponse user = userService.createAccount(request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .description(HttpStatus.CREATED.name())
                .data(Map.of(
                    "user", user,
                    "message", "Account creation successful. Created user must update their password on first login."
                ))
                .build()
        );
    }

    @PutMapping()
    @PreAuthorize("#id == authentication.principal.claims['sub'] or hasRole('SUPERVISOR')")
    public HttpEntity<HttpResponse> updateAccount(@RequestParam("id") UUID id, @RequestBody @Valid UserUpdateRequest request) {
        UserResponse user = userService.updateAccount(id, request);
        String message = user.getStatus().equals(Status.PENDING_VERIFICATION)
            ? "Account successfully updated. Please, log in and confirm your email."
            : "Account successfully updated.";

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of(
                    "user", user,
                    "message", message
                ))
                .build()
        );
    }

    @PutMapping("/role")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public HttpEntity<HttpResponse> updateRole(@RequestParam("id") UUID id, @RequestParam Role role) {
        UserResponse user = userService.updateRole(id, role);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("user", user))
                .build()
        );
    }

    @DeleteMapping()
    @PreAuthorize("#id == authentication.principal.claims['sub'] or hasRole('SUPERVISOR')")
    public HttpEntity<HttpResponse> deleteAccount(@RequestParam("id") UUID id) {
        Boolean accountDeleted = userService.deleteAccount(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(
                    accountDeleted
                        ? "Account has been deleted successfully."
                        : "Approval request has been created to delete account."
                )
                .build()
        );
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping("/internal/id/{id}")
    public HttpEntity<UserResponse> getUserByIdInternal(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
