package ai.examin.auth.model.user.service;

import ai.examin.auth.model.keycloak.service.KeycloakService;
import ai.examin.auth.model.notification.NotificationService;
import ai.examin.auth.model.token.repository.TokenRepository;
import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.auth.model.user.entity.User;
import ai.examin.auth.model.user.mapper.UserMapper;
import ai.examin.auth.model.user.repository.UserRepository;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Status;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final KeycloakService keycloakService;


    @Transactional
    public void register(UserRequest request) {
        if (userRepository.existsByUsernameAndStatusNot(request.username().trim().toLowerCase(), Status.DELETED))
            throw new ApiException(ResponseStatus.USERNAME_ALREADY_REGISTERED);

        if (userRepository.existsByEmailAndStatusNot(request.email().trim().toLowerCase(), Status.DELETED))
            throw new ApiException(ResponseStatus.EMAIL_ALREADY_REGISTERED);

        User user = UserMapper.toUser(request, passwordEncoder);
        userRepository.save(user);

        UserRepresentation registeredUser = keycloakService.registerUser(request);

        try {
            user.setExternalId(registeredUser.getId());
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to save externalId={} for user with email={}. Cause: {}",
                registeredUser.getId(), registeredUser.getEmail(), e.getMessage());
        }
    }

//    @Transactional
//    public void confirmEmail(String jwtToken) {
//        if (jwtToken == null)
//            throw new ApiException(ResponseStatus.TOKEN_REQUIRED);
//
//        String username = jwtProvider.extractUsernameFromToken(jwtToken);
//
//        if (username == null) {
//            tokenRepository.findByJwtTokenAndTypeAndRevokedFalseAndExpiredFalse(jwtToken, TokenType.ACCOUNT_VERIFICATION_TOKEN)
//                .ifPresent(token -> {
//                    token.setRevoked(true);
//                    tokenRepository.save(token);
//                });
//
//            throw new ApiException(ResponseStatus.INVALID_TOKEN);
//        }
//
//        if (jwtProvider.isTokenExpired(jwtToken)) {
//            tokenRepository.findByJwtTokenAndTypeAndRevokedFalseAndExpiredFalse(jwtToken, TokenType.ACCOUNT_VERIFICATION_TOKEN)
//                .ifPresent(token -> {
//                    token.setExpired(true);
//                    tokenRepository.save(token);
//                });
//
//            throw new ApiException(ResponseStatus.TOKEN_EXPIRED);
//        }
//
//        Token token = tokenRepository.findByJwtTokenAndType(jwtToken, TokenType.ACCOUNT_VERIFICATION_TOKEN)
//            .orElseThrow(() -> new ApiException(ResponseStatus.TOKEN_NOT_FOUND));
//
//        if (token.getExpired() || token.getRevoked())
//            throw new ApiException(ResponseStatus.TOKEN_EXPIRED);
//
//        User user = token.getUser();
//
//        if (!username.equals(user.getEmail()))
//            throw new ApiException(ResponseStatus.TOKEN_MISMATCH_EXCEPTION);
//
//        token.setRevoked(true);
//        tokenRepository.save(token);
//
//        user.setStatus(Status.ACTIVE);
//        userRepository.save(user);
//    }
//
//    public LoginResponse login(LoginRequest request) {
//        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
//            .orElseThrow(() -> new ApiException(ResponseStatus.EMAIL_OR_USERNAME_NOT_REGISTERED));
//
//        if (user.getStatus() == Status.PENDING_VERIFICATION)
//            throw new ApiException(ResponseStatus.EMAIL_CONFIRMATION_REQUIRED);
//
//        if (!passwordEncoder.matches(request.password(), user.getPassword()))
//            throw new ApiException(ResponseStatus.INCORRECT_PASSWORD);
//
//        List<Token> currentTokens = tokenRepository.findAllByUserAndTypeInAndRevokedFalseAndExpiredFalse(
//            user, List.of(TokenType.ACCESS_TOKEN, TokenType.REFRESH_TOKEN)
//        );
//        if (!currentTokens.isEmpty()) {
//            List<Token> revokedTokens = new LinkedList<>();
//            currentTokens.forEach(token -> {
//                token.setRevoked(true);
//                revokedTokens.add(token);
//            });
//            tokenRepository.saveAll(revokedTokens);
//        }
//
//        UserPrincipal userPrincipal = new UserPrincipal(UserMapper.getUserPayload(user));
//        String accessToken = jwtProvider.generateToken(userPrincipal, TokenType.ACCESS_TOKEN);
//        String refreshToken = jwtProvider.generateToken(userPrincipal, TokenType.REFRESH_TOKEN);
//
//        List<Token> tokens = new LinkedList<>();
//        tokens.add(TokenMapper.toToken(user, accessToken, TokenType.ACCESS_TOKEN));
//        tokens.add(TokenMapper.toToken(user, refreshToken, TokenType.REFRESH_TOKEN));
//        tokenRepository.saveAll(tokens);
//
//        LoginResponse loginResponse = new LoginResponse(user, accessToken, refreshToken);
//        log.info("Login response: {}", loginResponse);
//
//        return loginResponse;
//    }
}
