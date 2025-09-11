package ai.examin.auth.model.keycloak.mapper;

import ai.examin.auth.model.user.dto.AccountCreationRequest;
import ai.examin.auth.model.user.dto.UserRequest;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.BeanUtils;

import java.util.Collections;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class KeycloakMapper {

    public static UserRepresentation getUserRepresentation(UserRequest request) {
        UserRepresentation user = new UserRepresentation();
        BeanUtils.copyProperties(request, user, "password");
        user.setEnabled(TRUE);
        user.setEmailVerified(FALSE);
        user.setCredentials(Collections.singletonList(getCredentialRepresentation(request.getPassword(), FALSE)));
        return user;
    }

    public static UserRepresentation getUserRepresentation(AccountCreationRequest request) {
        UserRepresentation user = new UserRepresentation();
        BeanUtils.copyProperties(request, user, "role", "tempPassword");
        user.setEnabled(TRUE);
        user.setEmailVerified(TRUE);
        user.setCredentials(Collections.singletonList(getCredentialRepresentation(request.getTempPassword(), TRUE)));
        return user;
    }

    public static CredentialRepresentation getCredentialRepresentation(String password, Boolean isTemporary) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(password);
        credentials.setTemporary(isTemporary);

        return credentials;
    }

}
