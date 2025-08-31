package ai.examin.admin.model.course.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseUpdateNameRequest(
    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Description is required")
    String description
) {
}
