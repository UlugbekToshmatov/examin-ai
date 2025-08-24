package ai.examin.core.utils;

import ai.examin.core.security.UserContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class UserContextService {

    public Optional<String> getCurrentUserId() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("sub"));
    }

    public Optional<String> getCurrentUsername() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("preferred_username"));
    }

    public Optional<String> getCurrentUserEmail() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("email"));
    }

    public Collection<String> getCurrentUserRoles() {
        return getCurrentJwt()
            .map(this::extractRoles)
            .orElse(Collections.emptyList());
    }

    public Optional<UserContext> getCurrentUserContext() {
        return getCurrentJwt().map(jwt -> UserContext.builder()
            .externalId(jwt.getClaimAsString("sub"))
            .username(jwt.getClaimAsString("preferred_username"))
            .email(jwt.getClaimAsString("email"))
            .firstName(jwt.getClaimAsString("given_name"))
            .lastName(jwt.getClaimAsString("family_name"))
            .roles(extractRoles(jwt))
            .build());
    }

    private Optional<Jwt> getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            return Optional.of(jwtToken.getToken());
        }
        return Optional.empty();
    }

    private Collection<String> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        return realmAccess != null && realmAccess.containsKey("roles")
            ? (Collection<String>) realmAccess.get("roles")
            : Collections.emptyList();
    }
}
