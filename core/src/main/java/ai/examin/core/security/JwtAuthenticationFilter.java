package ai.examin.core.security;

import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.exception_handler.AuthExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String[] PUBLIC_ROUTES = {
        "/api/v1/user/login", "/api/v1/user/register", "/api/v1/user/resetpassword",
        "/api/v1/user/refresh/token"
    };
    private static final String HTTP_OPTIONS_METHOD = "OPTIONS";


    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String jwt = getToken(request);
            String userEmail = jwtProvider.extractUsernameFromToken(jwt);

            if (userEmail != null && !jwtProvider.isTokenExpired(jwt)) {
                UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(userEmail);

                if (userPrincipal.getUserPayload().getAccessToken() == null)
                    throw new ApiException(ResponseStatus.TOKEN_EXPIRED);

                if (!jwt.equals(userPrincipal.getUserPayload().getAccessToken()))
                    throw new ApiException(ResponseStatus.TOKEN_MISMATCH_EXCEPTION);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userPrincipal,
                    null,
                    userPrincipal.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
            } else {
                // Since authentication is required for all endpoints, throwing
                // exception instead of doing filterChain.doFilter(request, response);
                log.error("Subject is missing in JWT token: {}", jwt);
                throw new ApiException(ResponseStatus.INVALID_TOKEN);
            }
        } catch (ApiException e) {
            AuthExceptionHandler.handleAuthFailure(response, e, objectMapper);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        boolean shouldNotFilter = request.getHeader(AUTHORIZATION) == null || !request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX) ||
            request.getMethod().equalsIgnoreCase(HTTP_OPTIONS_METHOD) || Arrays.stream(PUBLIC_ROUTES).anyMatch(route -> request.getRequestURI().contains(route));
        log.info("Should Not Filter --> {}", shouldNotFilter);
        return shouldNotFilter;
    }

    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (bearerToken == null)
            throw new ApiException(ResponseStatus.TOKEN_NOT_FOUND);

        if (!bearerToken.startsWith(TOKEN_PREFIX))
            throw new ApiException(ResponseStatus.TOKEN_MUST_START_WITH_BEARER);

        return bearerToken.replace(TOKEN_PREFIX, EMPTY);
    }
}
