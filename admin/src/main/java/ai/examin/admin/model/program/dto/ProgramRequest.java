package ai.examin.admin.model.program.dto;

import jakarta.validation.constraints.NotBlank;

public record ProgramRequest(
    @NotBlank(message = "Program name is required")
    String name
){
}
