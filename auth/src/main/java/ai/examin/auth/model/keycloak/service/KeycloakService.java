package ai.examin.auth.model.keycloak.service;

import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.UserRole;
import ai.examin.core.exception_handler.ApiException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

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
                    verifyEmail(userId);
                    getUserResource(userId).roles().realmLevel().add(
                        Collections.singletonList(getRoleRepresentation(UserRole.INTERN.name()))
                    );
                    user.setId(userId);
                    return user;
                } else
                    throw new ApiException(ResponseStatus.ERROR_REGISTERING_USER);
            } else if (response.getStatus() == 409) {
                log.error("User with email: {} or username: {} already exists", request.email(), request.username());
                throw new ApiException(ResponseStatus.EMAIL_OR_USERNAME_ALREADY_REGISTERED);
            } else
                throw new ApiException(ResponseStatus.ERROR_REGISTERING_USER);
        } catch (Exception e) {
            log.error("Failed to register user with email: {} and username: {}. Cause: {}",
                request.email(), request.username(), e.getMessage());
            throw e;
        }
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
