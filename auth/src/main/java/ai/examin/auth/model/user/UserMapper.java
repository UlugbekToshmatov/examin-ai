package ai.examin.auth.model.user;

import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.auth.model.user.dto.UserResponse;
import ai.examin.core.enums.Status;
import ai.examin.core.enums.UserRole;
import ai.examin.core.security.UserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public static User toUser(UserRequest userRequest, PasswordEncoder passwordEncoder) {
        return User.builder()
            .firstName(userRequest.firstName())
            .lastName(userRequest.lastName())
            .email(userRequest.email().trim().toLowerCase())
            .password(passwordEncoder.encode(userRequest.password()))
            .role(UserRole.INTERN)
            .status(Status.PENDING_VERIFICATION)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .imageUrl(user.getImageUrl())
            .role(user.getRole())
            .createdAt(user.getCreatedAt())
            .build();
    }

    public static UserPayload getUserPayload(User user) {
        return UserPayload.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .password(user.getPassword())
            .role(user.getRole())
            .status(user.getStatus())
            .build();
    }

    public static UserPayload getUserPayload(User user, String jwtToken) {
        return UserPayload.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .password(user.getPassword())
            .role(user.getRole())
            .status(user.getStatus())
            .accessToken(jwtToken)
            .build();
    }
}
