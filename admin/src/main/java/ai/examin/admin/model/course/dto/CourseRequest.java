package ai.examin.admin.model.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CourseRequest(
    @NotNull(message = "Program ID is required")
    Long programId,

    UUID expertId,

    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Description is required")
    String description
) {
}
