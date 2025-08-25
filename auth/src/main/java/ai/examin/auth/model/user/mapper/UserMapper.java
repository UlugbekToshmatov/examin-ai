package ai.examin.auth.model.user.mapper;

import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.auth.model.user.dto.UserResponse;
import ai.examin.auth.model.user.entity.User;
import ai.examin.core.enums.Status;
import ai.examin.core.enums.UserRole;
import ai.examin.core.security.UserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public static User toEntity(UserRequest userRequest, PasswordEncoder passwordEncoder) {
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

    public static UserResponse toResponse(User user) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);

        return userResponse;
    }

    public static UserPayload getUserPayload(User user) {
        UserPayload userPayload = new UserPayload();
        BeanUtils.copyProperties(user, userPayload);

        return userPayload;
    }

    public static UserPayload getUserPayload(User user, String jwtToken) {
        UserPayload userPayload = new UserPayload();
        BeanUtils.copyProperties(user, userPayload);
        userPayload.setAccessToken(jwtToken);

        return userPayload;
    }
}
