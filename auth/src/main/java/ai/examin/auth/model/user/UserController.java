package ai.examin.auth.model.user;

import ai.examin.auth.model.user.dto.UserResponse;
import ai.examin.core.base_classes.HttpResponse;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


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

    @GetMapping("/internal/email/{email}")
    public HttpEntity<UserPrincipal> getUserByEmailInternal(@PathVariable String email) {
        UserDetails userDetails = userService.loadUserByUsername(email);
        return ResponseEntity.ok((UserPrincipal) userDetails);
    }
}
