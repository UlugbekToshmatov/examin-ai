package ai.examin.auth.model.user;

import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.auth.model.user.dto.UserResponse;
import dto.response.HttpResponse;
import enums.ResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public HttpEntity<HttpResponse> register(@RequestBody @Valid UserRequest userRequest) {
        Long userId = userService.register(userRequest);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .description(HttpStatus.CREATED.name())
                .data(Map.of("userId", userId))
                .build()
        );
    }

    // This API is for testing purposes
    @GetMapping("/email/{email}")
    public HttpEntity<HttpResponse> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.getUserByEmail(email);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("user", user))
                .build()
        );
    }
}
