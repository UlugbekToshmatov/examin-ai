package ai.examin.admin.model.task.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskUpdateTitleRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Definition is required")
    String definition
) {
}
