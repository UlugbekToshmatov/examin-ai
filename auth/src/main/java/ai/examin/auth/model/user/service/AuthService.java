package ai.examin.auth.model.user.service;

import ai.examin.auth.model.keycloak.service.KeycloakService;
import ai.examin.auth.model.user.dto.UpdatePasswordRequest;
import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.auth.model.user.entity.User;
import ai.examin.auth.model.user.repository.UserRepository;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Status;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.utils.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static ai.examin.auth.model.user.mapper.UserMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakService keycloakService;
    private final UserContextService userContextService;


    public void syncUserFromRegistration(String userId) {
        syncUserFromKeycloak(userId);
    }

    public void syncUserFromSocialLogin(String userId) {
        syncUserFromKeycloak(userId);
    }

    @Transactional
    public void register(UserRequest request) {
        trimRequest(request);

        if (userRepository.existsByUsernameAndStatusNot(request.getUsername(), Status.DELETED))
            throw new ApiException(ResponseStatus.USERNAME_ALREADY_REGISTERED);

        if (userRepository.existsByEmailAndStatusNot(request.getEmail(), Status.DELETED))
            throw new ApiException(ResponseStatus.EMAIL_ALREADY_REGISTERED);

        UserRepresentation registeredUser = keycloakService.registerUser(request);

        try {
            User user = toEntity(request, passwordEncoder, registeredUser.getId());
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to save user with email='{}'. Cause: {}", registeredUser.getEmail(), e.getMessage());
            keycloakService.deleteUser(registeredUser.getId());
        }
    }

    public void markEmailAsVerified(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        if (user.getStatus() != Status.ACTIVE) {
            log.info("Updating user status from {} to ACTIVE for user with id: {}", user.getStatus(), user.getId());
            user.setStatus(Status.ACTIVE);
            userRepository.save(user);
        }
    }

    public void forgotPassword(String email) {
        if (email.isEmpty())
            throw new ApiException(ResponseStatus.EMAIL_REQUIRED);

        User user = userRepository.findByEmailAndStatus(email.trim().toLowerCase(), Status.ACTIVE)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        keycloakService.forgotPassword(user.getId().toString());
    }

    public void updatePassword(UUID userId) {
        // TODO: redirect user to custom frontend interface to update password
        User user = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode("Password updated via Keycloak UI"));
        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(UUID id, UpdatePasswordRequest request) {
        User user = userRepository.findByIdAndStatus(id, Status.ACTIVE)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        trimPasswords(request);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new ApiException(ResponseStatus.INCORRECT_PASSWORD);

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new ApiException(ResponseStatus.PASSWORDS_DO_NOT_MATCH);

        if (request.getOldPassword().equals(request.getNewPassword()))
            throw new ApiException(ResponseStatus.PROVIDE_NEW_PASSWORD);

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context for user with id: {}", user.getId());
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        if (!Objects.equals(user.getId().toString(), currentUserId.get()))
            throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        keycloakService.resetPassword(user.getId().toString(), request.getNewPassword());
    }

    @Transactional
    public void deleteById(UUID id) {
        User user = userRepository.findByIdAndStatus(id, Status.ACTIVE)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context for user with id: {}", id);
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        user.softDelete(UUID.fromString(currentUserId.get()));
        userRepository.save(user);

        keycloakService.deleteUser(user.getId().toString());
    }

    private void syncUserFromKeycloak(String userId) {
        log.info("Syncing user data with id: {}", userId);
        try {
            // Wait for the user to be created in Keycloak
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error("Failed to sleep", e);
        }

        UserRepresentation userRepresentation = keycloakService.getById(userId);
        if (userRepresentation == null) {
            log.error("User with id: '{}' not found in Keycloak", userId);
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);
        }

        try {
            userRepository.save(toEntity(userRepresentation));
        } catch (Exception e) {
            log.error("Failed to save user with email='{}'. Cause: {}", userRepresentation.getEmail(), e.getMessage());
        }
    }
}
