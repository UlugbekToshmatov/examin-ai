package ai.examin.admin.model.course.dto;

import ai.examin.core.enums.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseUpdateRequest(
    @NotBlank(message = "Course name is required")
    String name,

    @NotNull(message = "Supervisor ID is required")
    Long supervisorId,

    @NotNull(message = "Course status is required")
    CourseStatus courseStatus
) {
}
