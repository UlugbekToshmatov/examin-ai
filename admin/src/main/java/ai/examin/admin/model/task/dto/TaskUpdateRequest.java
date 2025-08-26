package ai.examin.admin.model.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskUpdateRequest(
    @NotNull(message = "Mentor ID is required")
    Long mentorId,

    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Definition is required")
    String definition
) {
}
