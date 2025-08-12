package ai.examin.auth.model.user;

import ai.examin.auth.model.notification.NotificationService;
import ai.examin.auth.model.token.Token;
import ai.examin.auth.model.token.TokenMapper;
import ai.examin.auth.model.token.TokenRepository;
import ai.examin.auth.model.user.dto.LoginRequest;
import ai.examin.auth.model.user.dto.LoginResponse;
import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.auth.model.user.dto.UserResponse;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Status;
import ai.examin.core.enums.TokenType;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.security.JwtProvider;
import ai.examin.core.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    @Transactional
    public void register(UserRequest request) {
        if (userRepository.existsByEmailAndStatusNot(request.email().trim().toLowerCase(), Status.DELETED))
            throw new ApiException(ResponseStatus.EMAIL_ALREADY_REGISTERED);

        User user = UserMapper.toUser(request, passwordEncoder);
        userRepository.save(user);

        UserPrincipal userPrincipal = new UserPrincipal(UserMapper.getUserPayload(user));
        String jwtToken = jwtProvider.generateToken(userPrincipal, TokenType.ACCOUNT_VERIFICATION_TOKEN);
        tokenRepository.save(TokenMapper.toToken(user, jwtToken, TokenType.ACCOUNT_VERIFICATION_TOKEN));
        notificationService.send(getPayload(user, jwtToken));
    }

    @Transactional
    public void confirmEmail(String jwtToken) {
        if (jwtToken == null)
            throw new ApiException(ResponseStatus.TOKEN_REQUIRED);

        String username = jwtProvider.extractUsernameFromToken(jwtToken);

        if (username == null) {
            tokenRepository.findByJwtTokenAndTypeAndRevokedFalseAndExpiredFalse(jwtToken, TokenType.ACCOUNT_VERIFICATION_TOKEN)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    tokenRepository.save(token);
                });

            throw new ApiException(ResponseStatus.INVALID_TOKEN);
        }

        if (jwtProvider.isTokenExpired(jwtToken)) {
            tokenRepository.findByJwtTokenAndTypeAndRevokedFalseAndExpiredFalse(jwtToken, TokenType.ACCOUNT_VERIFICATION_TOKEN)
                .ifPresent(token -> {
                    token.setExpired(true);
                    tokenRepository.save(token);
                });

            throw new ApiException(ResponseStatus.TOKEN_EXPIRED);
        }

        Token token = tokenRepository.findByJwtTokenAndType(jwtToken, TokenType.ACCOUNT_VERIFICATION_TOKEN)
            .orElseThrow(() -> new ApiException(ResponseStatus.TOKEN_NOT_FOUND));

        if (token.getExpired() || token.getRevoked())
            throw new ApiException(ResponseStatus.TOKEN_EXPIRED);

        User user = token.getUser();

        if (!username.equals(user.getEmail()))
            throw new ApiException(ResponseStatus.TOKEN_MISMATCH_EXCEPTION);

        token.setRevoked(true);
        tokenRepository.save(token);

        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
            .orElseThrow(() -> new ApiException(ResponseStatus.EMAIL_NOT_REGISTERED));

        if (user.getStatus() == Status.PENDING_VERIFICATION)
            throw new ApiException(ResponseStatus.EMAIL_CONFIRMATION_REQUIRED);

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new ApiException(ResponseStatus.INCORRECT_PASSWORD);

        List<Token> currentTokens = tokenRepository.findAllByUserAndTypeInAndRevokedFalseAndExpiredFalse(
            user, List.of(TokenType.ACCESS_TOKEN, TokenType.REFRESH_TOKEN)
        );
        if (!currentTokens.isEmpty()) {
            List<Token> revokedTokens = new LinkedList<>();
            currentTokens.forEach(token -> {
                token.setRevoked(true);
                revokedTokens.add(token);
            });
            tokenRepository.saveAll(revokedTokens);
        }

        UserPrincipal userPrincipal = new UserPrincipal(UserMapper.getUserPayload(user));
        String accessToken = jwtProvider.generateToken(userPrincipal, TokenType.ACCESS_TOKEN);
        String refreshToken = jwtProvider.generateToken(userPrincipal, TokenType.REFRESH_TOKEN);

        List<Token> tokens = new LinkedList<>();
        tokens.add(TokenMapper.toToken(user, accessToken, TokenType.ACCESS_TOKEN));
        tokens.add(TokenMapper.toToken(user, refreshToken, TokenType.REFRESH_TOKEN));
        tokenRepository.saveAll(tokens);

        LoginResponse loginResponse = new LoginResponse(user, accessToken, refreshToken);
        log.info("Login response: {}", loginResponse);

        return loginResponse;
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

    public UserResponse getUserByEmail(String email) {
        UserResponse user = userRepository.findByEmailAndStatus(email, Status.ACTIVE)
            .map(UserMapper::fromUser)
            .orElseThrow(() -> new ApiException(ResponseStatus.USER_NOT_FOUND));

        log.info("User response by email: {}", user);

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
