package ai.examin.auth.model.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePasswordRequest(
    @NotNull(message = "Id is required")
    Long id,
    @NotBlank(message = "Old password is required")
    String oldPassword,
    @NotBlank(message = "New password is required")
    String newPassword,
    @NotBlank(message = "Confirm password is required")
    String confirmPassword
) {
}
