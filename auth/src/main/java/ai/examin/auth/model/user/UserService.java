package ai.examin.auth.model.user;

import ai.examin.auth.model.notification.NotificationService;
import ai.examin.auth.model.user.dto.UserRequest;
import ai.examin.auth.model.user.dto.UserResponse;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Status;
import ai.examin.core.exception_handler.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
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

        log.info("User details: {}", user);

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            true,
            true,
            true,
            !user.getStatus().equals(Status.BLOCKED),
            List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
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
