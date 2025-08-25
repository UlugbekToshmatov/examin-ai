package ai.examin.admin.model.program.dto;

import jakarta.validation.constraints.NotBlank;

public record ProgramDescriptionUpdateRequest(
    @NotBlank(message = "Description is required")
    String description
) {
}
