package ai.examin.auth.security_config;

import ai.examin.core.security.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfigBeans {

    // This bean is defined here in order to avoid circular dependencies error
    @Bean
    public JwtProvider jwtProvider() {
        return new JwtProvider();
    }

}
