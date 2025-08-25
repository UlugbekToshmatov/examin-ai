package ai.examin.admin.model.program.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProgramRequest(
    @NotNull(message = "Course ID is required")
    Long courseId,

    Long expertId,

    @NotBlank(message = "Description is required")
    String description
) {}
