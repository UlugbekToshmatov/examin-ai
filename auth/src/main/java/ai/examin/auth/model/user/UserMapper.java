package ai.examin.auth.model.user;

import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.auth.model.user.dto.UserResponse;
import ai.examin.core.enums.Status;
import ai.examin.core.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public static User fromRequest(UserRequest userRequest, PasswordEncoder passwordEncoder) {
        return User.builder()
            .firstName(userRequest.firstName())
            .lastName(userRequest.lastName())
            .username(userRequest.username())
            .email(userRequest.email().toLowerCase())
            .password(passwordEncoder.encode(userRequest.password()))
            .role(UserRole.INTERN)
            .status(Status.ACTIVE)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole())
            .createdAt(user.getCreatedAt())
            .build();
    }

}
