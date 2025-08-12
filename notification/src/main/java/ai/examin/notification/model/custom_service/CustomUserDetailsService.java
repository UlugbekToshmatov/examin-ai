package ai.examin.notification.model.custom_service;

import ai.examin.core.security.UserPrincipal;
import ai.examin.notification.model.clients.auth.AuthClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthClient authClient;


    @Override
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        return authClient.getUserByEmail(username);
    }
}
