package ai.examin.auth.model.user;

import ai.examin.auth.model.notification.NotificationService;
import ai.examin.auth.model.token.Token;
import ai.examin.auth.model.token.TokenRepository;
import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.auth.model.user.dto.UserResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;


    public Long register(UserRequest request) {
        if (userRepository.existsByEmailAndStatusNot(request.email(), Status.DELETED))
            throw new ApiException(ResponseStatus.EMAIL_ALREADY_REGISTERED);

        User user = UserMapper.toUser(request, passwordEncoder);
        userRepository.save(user);

        // TODO: send email with confirmation link to the provided email
        notificationService.sendNotification(getConfirmationLink(user));

        return user.getId();
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

    private Map<String, Object> getConfirmationLink(User user) {
        HashMap<String, Object> notification = new HashMap<>();
        notification.put("firstName", user.getFirstName());
        notification.put("email", user.getEmail());
        notification.put("confirmationLink", "https://google.com");

        return notification;
    }
}
