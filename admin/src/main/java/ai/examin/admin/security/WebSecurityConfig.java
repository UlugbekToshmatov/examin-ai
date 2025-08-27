package ai.examin.admin.security;

import ai.examin.core.enums.Role;
import ai.examin.core.exception_handler.CustomAccessDeniedHandler;
import ai.examin.core.exception_handler.CustomAuthenticationEntryPoint;
import ai.examin.core.security.keycloak.KeycloakJwtAuthConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/v1/course/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/program/**").permitAll()
                .requestMatchers("/api/v1/course/**").hasRole(Role.SUPERVISOR.name())
                .requestMatchers("/api/v1/program/**").hasAnyRole(Role.SUPERVISOR.name(), Role.EXPERT.name())
                .requestMatchers("/api/v1/task/**").hasAnyRole(Role.SUPERVISOR.name(), Role.MENTOR.name(), Role.INTERN.name())
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(
                oauth2 -> oauth2.jwt(
                    jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtAuthConverter())
                )
            )
            .sessionManagement(configurer ->
                configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            );

        return http.build();
    }
}
