package ai.examin.auth.model.keycloak.mapper;

import ai.examin.auth.model.user.dto.UserRequest;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class KeycloakMapper {

    public static UserRepresentation getUserRepresentation(UserRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username().trim().toLowerCase());
        user.setEmail(request.email().trim().toLowerCase());
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEnabled(TRUE);
        user.setEmailVerified(FALSE);
        user.setCredentials(Collections.singletonList(getCredentialRepresentation(request.password())));
        return user;
    }

    public static CredentialRepresentation getCredentialRepresentation(String password) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(password.trim());
        credentials.setTemporary(FALSE);
        return credentials;
    }

}
