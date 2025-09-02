package ai.examin.admin.model.clients.auth.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdminServiceInternalFeignClientConfig {
    private final AdminServiceApiClient adminServiceApiClient;


    @Bean("adminServiceRequestInterceptor")
    public RequestInterceptor serviceRequestInterceptor() {
        return requestTemplate -> {
            try {
                String adminServiceAccessToken = adminServiceApiClient.getAdminServiceAccessToken();
                String bearerToken = "Bearer " + adminServiceAccessToken;

                log.info("Adding service token to internal request --> {}", bearerToken);
                requestTemplate.header(HttpHeaders.AUTHORIZATION, bearerToken);
            } catch (Exception e) {
                log.error("Failed to add service token to request. Cause: {}", e.getMessage());
                throw e;
            }
        };
    }
}
