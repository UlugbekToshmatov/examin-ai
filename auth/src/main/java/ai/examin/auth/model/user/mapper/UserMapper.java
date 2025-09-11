package ai.examin.auth.model.user.mapper;

import ai.examin.auth.model.user.dto.*;
import ai.examin.auth.model.user.entity.User;
import ai.examin.core.enums.Status;
import ai.examin.core.enums.Role;
import ai.examin.core.security.UserPayload;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public static User toEntity(UserRequest userRequest, PasswordEncoder passwordEncoder, String id) {
        User user = new User();
        BeanUtils.copyProperties(userRequest, user, "password");
        user.setId(UUID.fromString(id));
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(Role.INTERN);
        user.setStatus(Status.PENDING_VERIFICATION);

        return user;
    }

    public static User toEntity(AccountCreationRequest request, PasswordEncoder passwordEncoder, String id) {
        User user = new User();
        BeanUtils.copyProperties(request, user, "tempPassword");
        user.setId(UUID.fromString(id));
        user.setPassword(passwordEncoder.encode(request.getTempPassword()));
        user.setRole(request.getRole());
        user.setStatus(Status.ACTIVE);

        return user;
    }

    public static User toEntity(UserRepresentation userRepresentation) {
        User newUser = new User();
        newUser.setId(UUID.fromString(userRepresentation.getId()));
        newUser.setUsername(userRepresentation.getUsername());
        newUser.setFirstName(userRepresentation.getFirstName());
        newUser.setLastName(userRepresentation.getLastName());
        newUser.setEmail(userRepresentation.getEmail());
        newUser.setPassword("User registered through social login");
        newUser.setStatus(userRepresentation.isEmailVerified() ? Status.ACTIVE : Status.PENDING_VERIFICATION);
        newUser.setRole(Role.INTERN);

        return newUser;
    }

    public static void update(User user, UserUpdateRequest userRequest) {
        if (!user.getEmail().equals(userRequest.getEmail()))
            user.setStatus(Status.PENDING_VERIFICATION);

        BeanUtils.copyProperties(userRequest, user);
    }

    public static UserResponse toResponse(User user) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);

        return userResponse;
    }

    public static UserPayload getUserPayload(User user, String jwtToken) {
        UserPayload userPayload = new UserPayload();
        BeanUtils.copyProperties(user, userPayload);
        userPayload.setAccessToken(jwtToken);

        return userPayload;
    }

    public static void trimRequest(UserRequest request) {
        request.setUsername(request.getUsername().trim().toLowerCase());
        request.setFirstName(request.getFirstName().trim());
        request.setLastName(request.getLastName().trim());
        request.setEmail(request.getEmail().trim().toLowerCase());
        request.setPassword(request.getPassword().trim());
    }

    public static void trimRequest(AccountCreationRequest request) {
        request.setUsername(request.getUsername().trim().toLowerCase());
        request.setFirstName(request.getFirstName().trim());
        request.setLastName(request.getLastName().trim());
        request.setEmail(request.getEmail().trim().toLowerCase());
    }

    public static void trimRequest(UserUpdateRequest request) {
        request.setUsername(request.getUsername().trim().toLowerCase());
        request.setFirstName(request.getFirstName().trim());
        request.setLastName(request.getLastName().trim());
        request.setEmail(request.getEmail().trim().toLowerCase());
    }

    public static void trimPasswords(UpdatePasswordRequest request) {
        request.setOldPassword(request.getOldPassword().trim());
        request.setNewPassword(request.getNewPassword().trim());
        request.setConfirmPassword(request.getConfirmPassword().trim());
    }
}
