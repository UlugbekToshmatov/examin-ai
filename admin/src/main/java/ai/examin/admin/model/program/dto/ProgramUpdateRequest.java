package ai.examin.admin.model.program.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProgramUpdateRequest(
    @NotNull(message = "Expert ID is required")
    Long expertId,

    @NotBlank(message = "Description is required")
    String description
) {
}
