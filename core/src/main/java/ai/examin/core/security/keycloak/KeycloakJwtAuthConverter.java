package ai.examin.core.security.keycloak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class KeycloakJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String REALM_ROLES = "realm_access";
    private static final String ROLES = "roles";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        String principal = getPrincipal(jwt);
        log.info("\nAuthorities --> {}\nPrincipal --> {}", authorities, principal);
        return new JwtAuthenticationToken(jwt, authorities, principal);
    }

    private String getPrincipal(Jwt jwt) {
        // preferred_username if present, otherwise sub
        return Optional.ofNullable(jwt.getClaimAsString("preferred_username"))
            .orElse(jwt.getSubject());
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        // 1) realm roles
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ROLES);
        if (realmAccess != null) {
            Object r = realmAccess.get(ROLES);
            if (r instanceof Collection<?> col) {
                col.forEach(v -> roles.add(String.valueOf(v)));
            }
        }

        // 2) clients/resource roles (for any clients)
        Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
        if (resourceAccess != null) {
            resourceAccess.values().forEach(val -> {
                if (val instanceof Map<?, ?> m) {
                    Object rr = m.get(ROLES);
                    if (rr instanceof Collection<?> col) {
                        col.forEach(v -> roles.add(String.valueOf(v)));
                    }
                }
            });
        }

        return roles.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(s -> s.startsWith(ROLE_PREFIX) ? s : ROLE_PREFIX + s)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }
}
