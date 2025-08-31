package ai.examin.admin.model.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TaskRequest(
    @NotNull(message = "Course ID is required")
    Long courseId,

    UUID mentorId,

    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Definition is required")
    String definition
) {
}
