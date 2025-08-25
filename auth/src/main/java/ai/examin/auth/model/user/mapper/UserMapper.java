package ai.examin.auth.model.user.mapper;

import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.auth.model.user.dto.UserResponse;
import ai.examin.auth.model.user.entity.User;
import ai.examin.core.enums.Status;
import ai.examin.core.enums.UserRole;
import ai.examin.core.security.UserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public static User toUser(UserRequest userRequest, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setExternalId("dummyValue");
        user.setUsername(userRequest.username().trim().toLowerCase());
        user.setFirstName(userRequest.firstName().trim());
        user.setLastName(userRequest.lastName().trim());
        user.setEmail(userRequest.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(userRequest.password().trim()));
        user.setRole(UserRole.INTERN);
        user.setStatus(Status.PENDING_VERIFICATION);

        return user;
    }

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .externalId(user.getExternalId())
            .username(user.getUsername())
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
