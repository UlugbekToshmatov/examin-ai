package ai.examin.admin.model.profile.controller;

import ai.examin.admin.model.clients.auth.dto.AccountCreationRequest;
import ai.examin.admin.model.profile.dto.UserUpdateRequest;
import ai.examin.admin.model.profile.service.ProfileService;
import ai.examin.core.base_classes.HttpResponse;
import ai.examin.core.enums.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;


    @GetMapping()
    public HttpEntity<HttpResponse> getById(@RequestParam("id") UUID id) {
        return ResponseEntity.ok(profileService.getAccountById(id));
    }

    @PostMapping("/create")
    public HttpEntity<HttpResponse> createAccount(@RequestBody @Valid AccountCreationRequest request) {
        return ResponseEntity.ok(profileService.createAccount(request));
    }

    @PutMapping()
    public HttpEntity<HttpResponse> updateAccount(@RequestParam("id") UUID id, @RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(profileService.updateAccountById(id, request));
    }

    @PutMapping("/role")
    public HttpEntity<HttpResponse> updateRole(@RequestParam("id") UUID id, @RequestParam Role role) {
        return ResponseEntity.ok(profileService.updateRoleById(id, role));
    }

    @DeleteMapping()
    public HttpEntity<HttpResponse> deleteAccount(@RequestParam("id") UUID id) {
        return ResponseEntity.ok(profileService.deleteAccountById(id));
    }
}
