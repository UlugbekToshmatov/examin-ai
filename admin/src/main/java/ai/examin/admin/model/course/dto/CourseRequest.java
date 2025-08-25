package ai.examin.admin.model.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseRequest(
    @NotBlank(message = "Course name is required")
    String name,

    @NotNull(message = "Supervisor ID is required")
    Long supervisorId
){
}
