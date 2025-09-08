package ai.examin.admin.model.course.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseUpdateRequest(
    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Description is required")
    String description
) {
}
