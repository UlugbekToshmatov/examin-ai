package ai.examin.auth.model.keycloak.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakEvent {
    private String eventType;
    private String userId;
    private String timestamp;
    private Map<String, String> details;
}
