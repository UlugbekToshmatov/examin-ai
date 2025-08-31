package ai.examin.admin.model.program.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProgramRequest(
    @NotBlank(message = "Program name is required")
    String name,

    @NotNull(message = "Supervisor ID is required")
    UUID supervisorId
){
}
