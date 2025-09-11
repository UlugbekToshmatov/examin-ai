package ai.examin.auth.model.user.service;

import ai.examin.auth.model.keycloak.service.KeycloakService;
import ai.examin.auth.model.token.entity.Token;
import ai.examin.auth.model.token.repository.TokenRepository;
import ai.examin.auth.model.user.dto.AccountCreationRequest;
import ai.examin.auth.model.user.dto.UserResponse;
import ai.examin.auth.model.user.dto.UserUpdateRequest;
import ai.examin.auth.model.user.entity.User;
import ai.examin.auth.model.user.mapper.UserMapper;
import ai.examin.auth.model.user.repository.UserRepository;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Role;
import ai.examin.core.enums.Status;
import ai.examin.core.enums.TokenType;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.security.UserContext;
import ai.examin.core.security.UserPrincipal;
import ai.examin.core.utils.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static ai.examin.auth.model.user.mapper.UserMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final UserContextService userContextService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;


    public UserResponse getUserById(UUID id) {
        UserResponse user = userRepository.findByIdAndStatus(id, Status.ACTIVE)
            .map(UserMapper::toResponse)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        log.info("User response by id: {}", user);

        return user;
    }

    public UserResponse createAccount(AccountCreationRequest request) {
        trimRequest(request);

        if (request.getRole().equals(Role.SUPERVISOR))
            throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

        if (userRepository.existsByUsernameAndStatusNot(request.getUsername(), Status.DELETED))
            throw new ApiException(ResponseStatus.USERNAME_ALREADY_REGISTERED);

        if (userRepository.existsByEmailAndStatusNot(request.getEmail(), Status.DELETED))
            throw new ApiException(ResponseStatus.EMAIL_ALREADY_REGISTERED);

        UserRepresentation user = keycloakService.createAccount(request);
        try {
            User createdAccount = toEntity(request, passwordEncoder, user.getId());
            userRepository.save(createdAccount);
            return toResponse(createdAccount);
        } catch (Exception e) {
            log.error("Failed to save user with email='{}'. Cause: {}", user.getEmail(), e.getMessage());
            keycloakService.deleteUser(user.getId());
        }

        return null;
    }

    @Transactional
    public UserResponse updateAccount(UUID id, UserUpdateRequest request) {
        User user = userRepository.findByIdAndStatus(id, Status.ACTIVE)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        trimRequest(request);

        if (
            !user.getEmail().equals(request.getEmail()) &&
            userRepository.existsByEmailAndStatusNot(request.getEmail(), Status.DELETED)
        )
            throw new ApiException(ResponseStatus.EMAIL_ALREADY_REGISTERED);

        if (
            !user.getUsername().equals(request.getUsername()) &&
            userRepository.existsByUsernameAndStatusNot(request.getUsername(), Status.DELETED)
        )
            throw new ApiException(ResponseStatus.USERNAME_ALREADY_REGISTERED);

        if (getCurrentUserContext().getRoles().contains(Role.SUPERVISOR.name())) {
            update(user, request);
            userRepository.save(user);

            keycloakService.updateUser(user.getId().toString(), request);
        }/* else {
            // TODO: Send approval request to supervisor to update account
        }*/

        return toResponse(user);
    }

    @Transactional
    public UserResponse updateRole(UUID id, Role role) {
        if (role.equals(Role.SUPERVISOR))
            throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

        if (!getCurrentUserContext().getRoles().contains(Role.SUPERVISOR.name()))
            throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

        User user = userRepository.findByIdAndStatus(id, Status.ACTIVE)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        user.setRole(role);
        userRepository.save(user);

        keycloakService.updateUserRole(user.getId().toString(), role);

        return toResponse(user);
    }

    @Transactional
    public Boolean deleteAccount(UUID id) {
        User user = userRepository.findByIdAndStatus(id, Status.ACTIVE)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        UserContext currentUser = getCurrentUserContext();

        if (currentUser.getRoles().contains(Role.SUPERVISOR.name())) {
            user.softDelete(currentUser.getId());
            userRepository.save(user);

            keycloakService.deleteUser(user.getId().toString());

            return true;
        } else {
            // TODO: Send approval request to supervisor to delete account
            return false;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndStatus(username, Status.ACTIVE)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        String jwtToken = null;
        Token token = tokenRepository.findByUser_IdAndTypeAndRevokedFalseAndExpiredFalse(user.getId(), TokenType.ACCESS_TOKEN)
            .orElse(null);

        if (token != null) {
            if (LocalDateTime.now().isAfter(token.getExpiresAt())) {
                token.setExpired(true);
                tokenRepository.save(token);
            } else
                jwtToken = token.getJwtToken();
        }

        UserPrincipal userPrincipal = new UserPrincipal(UserMapper.getUserPayload(user, jwtToken));
        log.info("User principal details: {}", userPrincipal);

        return userPrincipal;
    }

    private UserContext getCurrentUserContext() {
        Optional<UserContext> userContext = userContextService.getCurrentUserContext();
        if (userContext.isEmpty()) {
            log.error("User details are not present in the context in UserService.getCurrentUserContext()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        return userContext.get();
    }

//    private Map<String, Object> getPayload(User user, String jwtToken) {
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.put("firstName", user.getFirstName());
//        payload.put("lastName", user.getLastName());
//        payload.put("email", user.getEmail());
//        payload.put("link", "http://localhost:8070/api/v1/auth/confirm/account?token=" + jwtToken);
//
//        return payload;
//    }
}
