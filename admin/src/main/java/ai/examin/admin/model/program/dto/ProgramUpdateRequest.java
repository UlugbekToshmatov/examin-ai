package ai.examin.admin.model.program.dto;

import ai.examin.core.enums.ProgramStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProgramUpdateRequest(
    @NotBlank(message = "Program name is required")
    String name,

    @NotNull(message = "Supervisor ID is required")
    UUID supervisorId,

    @NotNull(message = "Program status is required")
    ProgramStatus programStatus
) {
}
