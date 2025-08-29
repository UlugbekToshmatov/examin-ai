package ai.examin.auth.model.user.controller;

import ai.examin.auth.model.user.dto.UpdatePasswordRequest;
import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.auth.model.user.service.AuthService;
import ai.examin.core.base_classes.HttpResponse;
import ai.examin.core.enums.ResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    @PostMapping("/register")
    public HttpEntity<HttpResponse> register(@RequestBody @Valid UserRequest userRequest) {
        authService.register(userRequest);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .description(HttpStatus.CREATED.name())
                .data("Registration successful. Please, check your email to verify your account.")
                .build()
        );
    }

    @GetMapping("/forgot-password")
    public HttpEntity<HttpResponse> forgotPassword(@RequestParam(value = "email") String email) {
        authService.forgotPassword(email);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data("A link has been sent to your email to reset your password.")
                .build()
        );
    }

    @PutMapping("/reset-password")
    public HttpEntity<HttpResponse> updatePassword(@RequestParam UUID id, @RequestBody @Valid UpdatePasswordRequest request) {
        authService.resetPassword(id, request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data("Password has been reset successfully.")
                .build()
        );
    }

    @DeleteMapping()
    public HttpEntity<HttpResponse> deleteAccount(@RequestParam UUID id) {
        authService.deleteById(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data("Account has been deleted successfully.")
                .build()
        );
    }
}
