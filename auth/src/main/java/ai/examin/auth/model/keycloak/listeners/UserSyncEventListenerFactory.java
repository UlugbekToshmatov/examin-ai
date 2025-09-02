package ai.examin.auth.model.keycloak.listeners;

import ai.examin.auth.model.keycloak.clients.AuthServiceApiClient;
import ai.examin.core.utils.RestTemplateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSyncEventListenerFactory implements EventListenerProviderFactory {
    private final AuthServiceApiClient authServiceApiClient;
    private final RestTemplateUtils restTemplateUtils;
    private final ObjectMapper objectMapper;


    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new UserSyncEventListener(authServiceApiClient, restTemplateUtils, objectMapper);
    }

    @Override
    public void init(Config.Scope config) {
        // Initialize any configuration if needed
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Post-initialization if needed
    }

    @Override
    public void close() {
        // Cleanup if needed
    }

    @Override
    public String getId() {
        // Return the ID that appears in Keycloak Admin Console
        return "user-sync-event-listener";
    }
}
