package ai.examin.gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;

@Configuration
public class TokenRelayConfig {

    // You only need this if you're using oauth2Login() in the Gateway
    // If your client (frontend, Postman) directly sends a JWT in the Authorization header, this relay is not
    // necessary — just configure spring.security.oauth2.resourceserver.jwt.issuer-uri and the gateway will handle the rest
    @Bean
    public GlobalFilter tokenRelayFilter(ServerOAuth2AuthorizedClientRepository clientRepository) {
        return (exchange, chain) -> exchange.getPrincipal()
            .cast(OAuth2AuthenticationToken.class)
            .flatMap(auth -> clientRepository
                .loadAuthorizedClient(auth.getAuthorizedClientRegistrationId(), auth, exchange)
                .cast(OAuth2AuthorizedClient.class)
            )
            .map(OAuth2AuthorizedClient::getAccessToken)
            .map(token -> {
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                    .build();
                return exchange.mutate().request(mutatedRequest).build();
            })
            .flatMap(chain::filter);
    }

}

