package ai.examin.auth.model.keycloak.clients.config;

import ai.examin.auth.model.keycloak.clients.AuthServiceApiClient;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthServiceInternalFeignClientConfig {
    private final AuthServiceApiClient authServiceApiClient;


    @Bean("authServiceRequestInterceptor")
    public RequestInterceptor serviceRequestInterceptor() {
        return requestTemplate -> {
            try {
                String authServiceAccessToken = authServiceApiClient.getAuthServiceAccessToken();
                String bearerToken = "Bearer " + authServiceAccessToken;

                log.info("Adding service token to internal request --> {}", bearerToken);
                requestTemplate.header(HttpHeaders.AUTHORIZATION, bearerToken);
            } catch (Exception e) {
                log.error("Failed to add service token to request. Cause: {}", e.getMessage());
                throw e;
            }
        };
    }
}
