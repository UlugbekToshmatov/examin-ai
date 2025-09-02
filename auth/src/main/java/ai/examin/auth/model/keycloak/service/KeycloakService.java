package ai.examin.auth.model.keycloak.service;

import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Role;
import ai.examin.core.exception_handler.ApiException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static ai.examin.auth.model.keycloak.mapper.KeycloakMapper.getCredentialRepresentation;
import static ai.examin.auth.model.keycloak.mapper.KeycloakMapper.getUserRepresentation;
import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;


    public UserRepresentation registerUser(UserRequest request) {
        UserRepresentation user = getUserRepresentation(request);

        try (Response response = getUsersResource().create(user)) {
            if (Objects.equals(response.getStatus(), 201)) {
                if (response.getLocation() != null) {
                    String userId = response.getLocation().getPath().substring(
                        response.getLocation().getPath().lastIndexOf('/') + 1
                    );
                    CompletableFuture.runAsync(() -> verifyEmail(userId));
                    getUserResource(userId).roles().realmLevel().add(
                        Collections.singletonList(getRoleRepresentation(Role.INTERN.name()))
                    );
                    user.setId(userId);
                    return user;
                } else
                    throw new ApiException(ResponseStatus.ERROR_REGISTERING_USER);
            } else if (response.getStatus() == 409) {
                log.error("User with email: {} or username: {} already exists", request.getEmail(), request.getUsername());
                throw new ApiException(ResponseStatus.EMAIL_OR_USERNAME_ALREADY_REGISTERED);
            } else
                throw new ApiException(ResponseStatus.ERROR_REGISTERING_USER);
        } catch (Exception e) {
            log.error("Failed to register user with email: {} and username: {}. Cause: {}",
                request.getEmail(), request.getUsername(), e.getMessage());
            throw e;
        }
    }

    public void forgotPassword(String userId) {
        String UPDATE_PASSWORD = "UPDATE_PASSWORD";
        UserResource user = getUserById(userId);
        CompletableFuture.runAsync(() -> user.executeActionsEmail(Collections.singletonList(UPDATE_PASSWORD)));
    }

    public void resetPassword(String userId, String password) {
        UserResource user = getUserById(userId);
        CredentialRepresentation credentialRepresentation = getCredentialRepresentation(password);
        user.resetPassword(credentialRepresentation);
    }

    public void deleteUser(String userId) {
        UserResource user = getUserById(userId);
        user.remove();
    }

    private UserResource getUserById(String userId) {
        UserResource user = getUserResource(userId);

        if (user == null) {
            log.error("User with id: '{}' not found in Keycloak", userId);
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);
        }
        return user;
    }

    public UserRepresentation getById(String userId) {
        return getUsersResource().search(userId, 1, 1, TRUE)
            .stream()
            .findFirst()
            .orElse(null);
    }

    private UserRepresentation getUserByUsername(String username) {
        return getUsersResource()
            .searchByUsername(username.trim().toLowerCase(), TRUE)
            .stream()
//            .filter(user -> Objects.equals(user.isEmailVerified(), FALSE))
            .findFirst()
            .orElse(null);
    }

    private void verifyEmail(String userId) {
        getUsersResource().get(userId).sendVerifyEmail();
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    private UserResource getUserResource(String userId) {
        return getUsersResource().get(userId);
    }

    private RolesResource getRolesResource() {
        return keycloak.realm(realm).roles();
    }

    private RoleRepresentation getRoleRepresentation(String roleName) {
        return getRolesResource().get(roleName).toRepresentation();
    }
}
