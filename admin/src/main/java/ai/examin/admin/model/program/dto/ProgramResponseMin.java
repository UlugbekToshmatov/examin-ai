package ai.examin.admin.model.program.dto;

import ai.examin.admin.model.program.entity.Program;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramResponseMin {
    private Long id;
    private String description;


    public ProgramResponseMin(Program program) {
        this.id = program.getId();
        this.description = program.getDescription();
    }
}
