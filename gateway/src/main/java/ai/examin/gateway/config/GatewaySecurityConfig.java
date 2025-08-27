package ai.examin.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/v1/auth/**").permitAll()
                .pathMatchers("/api/v1/user/**").hasRole("INTERN")
                .pathMatchers("/api/v1/tasks/**").hasAnyRole("MENTOR", "INTERN")
                .pathMatchers("/api/v1/courses/**").hasRole("SUPERVISOR")
//                .pathMatchers("/eureka/**").hasRole("SUPERVISOR")
                .pathMatchers("/eureka/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            );

        return http.build();
    }

    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        return jwt -> {
             Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
             Collection<String> roles = (Collection<String>) realmAccess.get("roles");

            Collection<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());

            return Mono.just(new JwtAuthenticationToken(jwt, authorities));
        };
    }

    @Slf4j
    @Component
    public static class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

        @Override
        public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
            log.error("Authentication failed: {}", ex.getMessage());

            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add(CONTENT_TYPE, APPLICATION_JSON_VALUE);

            String body = createErrorResponse(ex);
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

            return response.writeWith(Mono.just(buffer));
        }

        private String createErrorResponse(AuthenticationException ex) {
            String errorCode = determineErrorCode(ex);
            String message = determineErrorMessage(ex);

            return String.format("""
                {
                    "error": "AUTHENTICATION_FAILED",
                    "code": "%s",
                    "message": "%s",
                    "timestamp": "%s",
                    "path": "/gateway"
                }
                """, errorCode, message, Instant.now().toString());
        }

        private String determineErrorCode(AuthenticationException ex) {
            if (ex.getCause() instanceof JwtException || ex.getCause() instanceof JwtException) {
                if (ex.getMessage().contains("expired") ||
                    (ex.getCause() != null && ex.getCause().getMessage().contains("expired"))) {
                    return "TOKEN_EXPIRED";
                }
                if (ex.getMessage().contains("invalid") ||
                    (ex.getCause() != null && ex.getCause().getMessage().contains("invalid"))) {
                    return "TOKEN_INVALID";
                }
                return "TOKEN_ERROR";
            }
            return "AUTHENTICATION_ERROR";
        }

        private String determineErrorMessage(AuthenticationException ex) {
            String errorCode = determineErrorCode(ex);
            return switch (errorCode) {
                case "TOKEN_EXPIRED" -> "Your session has expired. Please login again.";
                case "TOKEN_INVALID" -> "Invalid authentication token provided.";
                case "TOKEN_ERROR" -> "Authentication token error occurred.";
                default -> "Authentication failed. Please check your credentials.";
            };
        }
    }

    @Slf4j
    @Component
    public static class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {

        @Override
        public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
            log.error("Access denied: {}", denied.getMessage());

            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().add(CONTENT_TYPE, APPLICATION_JSON_VALUE);

            String body = String.format("""
                {
                    "error": "ACCESS_DENIED",
                    "code": "INSUFFICIENT_PRIVILEGES",
                    "message": "You don't have sufficient privileges to access this resource.",
                    "timestamp": "%s",
                    "path": "%s"
                }
                """, Instant.now().toString(), exchange.getRequest().getPath().value());

            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }
    }
}

