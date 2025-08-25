package ai.examin.auth.model.user.service;

import ai.examin.auth.model.token.entity.Token;
import ai.examin.auth.model.token.repository.TokenRepository;
import ai.examin.auth.model.user.dto.UserResponse;
import ai.examin.auth.model.user.entity.User;
import ai.examin.auth.model.user.mapper.UserMapper;
import ai.examin.auth.model.user.repository.UserRepository;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Status;
import ai.examin.core.enums.TokenType;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;


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

    public UserResponse getUserByEmail(String email) {
        UserResponse user = userRepository.findByEmailAndStatus(email, Status.ACTIVE)
            .map(UserMapper::toResponse)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        log.info("User response by email: {}", user);

        return user;
    }

    public UserResponse getUserById(Long id) {
        UserResponse user = userRepository.findByIdAndStatus(id, Status.ACTIVE)
            .map(UserMapper::toResponse)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        log.info("User response by id: {}", user);

        return user;
    }

    public UserResponse getUserByExternalId(String externalId) {
        UserResponse user = userRepository.findByExternalIdAndStatus(externalId, Status.ACTIVE)
            .map(UserMapper::toResponse)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        log.info("User response by external id: {}", user);

        return user;
    }

    private Map<String, Object> getPayload(User user, String jwtToken) {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("firstName", user.getFirstName());
        payload.put("lastName", user.getLastName());
        payload.put("email", user.getEmail());
        payload.put("link", "http://localhost:8070/api/v1/auth/confirm/account?token=" + jwtToken);

        return payload;
    }
}
