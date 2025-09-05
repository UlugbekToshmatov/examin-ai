package ai.examin.auth.model.keycloak.controller;

import ai.examin.auth.model.keycloak.dto.KeycloakEvent;
import ai.examin.auth.model.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/sync")
@RequiredArgsConstructor
public class KeycloakSyncController {
    private final AuthService authService;


    @PostMapping("/user-event")
    public ResponseEntity<Void> handleUserEvent(@RequestBody KeycloakEvent event) {
        log.info("Received Keycloak event: {} for user: {}", event.getEventType(), event.getUserId());

        try {
            switch (event.getEventType()) {
                case "USER_REGISTERED":
                    authService.syncUserFromRegistration(event.getUserId());
                    break;
                case "SOCIAL_LOGIN":
                    authService.syncUserFromSocialLogin(event.getUserId());
                    break;
                case "EMAIL_VERIFIED":
                    authService.markEmailAsVerified(UUID.fromString(event.getUserId()));
                    break;
                case "PASSWORD_UPDATED":
                    authService.updatePassword(UUID.fromString(event.getUserId()));
                    break;
                case "USER_DELETED_ADMIN":
                    authService.deleteById(UUID.fromString(event.getUserId()));
                    break;
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to process Keycloak event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
