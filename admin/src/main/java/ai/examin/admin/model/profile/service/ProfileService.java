package ai.examin.admin.model.profile.service;

import ai.examin.admin.model.clients.auth.AuthClient;
import ai.examin.admin.model.clients.auth.dto.AccountCreationRequest;
import ai.examin.admin.model.profile.dto.UserUpdateRequest;
import ai.examin.core.base_classes.HttpResponse;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Role;
import ai.examin.core.exception_handler.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final AuthClient authClient;


    public HttpResponse getAccountById(UUID id) {
        HttpResponse httpResponse = authClient.getUserById(id);
        logResponse(httpResponse);
        return httpResponse;
    }

    public HttpResponse createAccount(AccountCreationRequest request) {
        HttpResponse httpResponse = authClient.createAccount(request);
        logResponse(httpResponse);
        Map<String, Object> data = (Map) httpResponse.getData();if (data.get("user") == null)
            throw new ApiException(ResponseStatus.ERROR_REGISTERING_USER);

        return httpResponse;
    }

    public HttpResponse updateAccountById(UUID id, UserUpdateRequest request) {
        HttpResponse httpResponse = authClient.updateAccount(id, request);
        logResponse(httpResponse);
        return httpResponse;
    }

    public HttpResponse updateRoleById(UUID id, Role role) {
        HttpResponse httpResponse = authClient.updateRole(id, role);
        logResponse(httpResponse);
        return httpResponse;
    }

    public HttpResponse deleteAccountById(UUID id) {
        HttpResponse httpResponse = authClient.deleteAccount(id);
        logResponse(httpResponse);
        return httpResponse;
    }

    private static void logResponse(HttpResponse httpResponse) {
        log.info("Http response: {}", httpResponse);
    }
}
