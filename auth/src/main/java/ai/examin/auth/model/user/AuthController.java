package ai.examin.auth.model.user;

import ai.examin.auth.model.user.dto.LoginRequest;
import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.auth.model.user.dto.UserResponse;
import ai.examin.core.base_classes.HttpResponse;
import ai.examin.core.enums.ResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;


    @PostMapping("/register")
    public HttpEntity<HttpResponse> register(@RequestBody @Valid UserRequest userRequest) {
        userService.register(userRequest);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .description(HttpStatus.CREATED.name())
                .data("Registration successful. Please, check your email to verify your account.")
                .build()
        );
    }

    @GetMapping("/confirm/account")
    public HttpEntity<HttpResponse> confirm(@RequestParam(value = "token") String jwtToken) {
        userService.confirmEmail(jwtToken);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data("You have successfully confirmed your email. You can now log in to your account.")
                .build()
        );
    }

    @PostMapping("/login")
    public HttpEntity<HttpResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(userService.login(request))
                .build()
        );
    }
}
