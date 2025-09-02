package ai.examin.auth.model.keycloak.listeners;

import ai.examin.auth.model.keycloak.clients.AuthServiceApiClient;
import ai.examin.auth.model.keycloak.dto.KeycloakEvent;
import ai.examin.core.utils.RestTemplateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSyncEventListener implements EventListenerProvider {
    private final AuthServiceApiClient authServiceApiClient;
    private final RestTemplateUtils restTemplateUtils;
    private final ObjectMapper objectMapper;
    @Value("${keycloak.user.sync.webhook.url}")
    private String webhookUrl;

    @Override
    public void onEvent(Event event) {
        // Handle user events
        switch (event.getType()) {
            case VERIFY_EMAIL:
                sendUserEvent("EMAIL_VERIFIED", event.getUserId(), event.getDetails());
                break;
            case UPDATE_PASSWORD:
                sendUserEvent("PASSWORD_UPDATED", event.getUserId(), event.getDetails());
                break;
            case LOGIN:
                // Only sync on social login
                if (event.getDetails() != null &&
                    (event.getDetails().containsKey("identity_provider") ||
                        event.getDetails().containsKey("identity_provider_identity"))) {
                    sendUserEvent("SOCIAL_LOGIN", event.getUserId(), event.getDetails());
                }
                break;
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        // Handle admin events (user updates via admin console)
    }

    @Override
    public void close() {
        // Cleanup if needed
    }

    private void sendUserEvent(String eventType, String userId, Map<String, String> details) {
        try {
            HttpHeaders headers = getHeaders();
            KeycloakEvent payload = getPayload(eventType, userId, details);
            ResponseEntity<String> response = restTemplateUtils.execute(webhookUrl, payload, headers, HttpMethod.POST);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully synced user event: {}. Response: {}",
                    eventType, objectMapper.readValue(response.getBody(), Object.class));
            }
        } catch (Exception e) {
            // Log error but don't fail Keycloak operation
            log.error("Failed to send user event: {}", e.getMessage());
        }
    }

    private HttpHeaders getHeaders() {
        String authServiceAccessToken = authServiceApiClient.getAuthServiceAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authServiceAccessToken);

        return headers;
    }

    private KeycloakEvent getPayload(String eventType, String userId, Map<String, String> details) {
        KeycloakEvent keycloakEvent = new KeycloakEvent();
        keycloakEvent.setEventType(eventType);
        keycloakEvent.setUserId(userId);
        keycloakEvent.setTimestamp(Instant.now().toString());
        keycloakEvent.setDetails(details);

        return keycloakEvent;
    }
}
