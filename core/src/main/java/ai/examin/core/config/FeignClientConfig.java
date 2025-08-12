package ai.examin.core.config;

import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.exception_handler.ApiException;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Slf4j
@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Get current HTTP request (bound to thread)
            HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest();

            String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
            log.info("Bearer Token --> {}", bearerToken);

            if (bearerToken == null)
                throw new ApiException(ResponseStatus.TOKEN_REQUIRED);

            if (!bearerToken.startsWith("Bearer "))
                throw new ApiException(ResponseStatus.TOKEN_MUST_START_WITH_BEARER);

            requestTemplate.header(HttpHeaders.AUTHORIZATION, bearerToken);
        };
    }
}

