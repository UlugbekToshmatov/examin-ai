package ai.examin.admin.model.program.dto;

import ai.examin.admin.model.program.entity.Program;
import ai.examin.admin.model.program.entity.ProgramExpert;
import ai.examin.core.enums.ProgramStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramResponse {
    private Long id;
    private String name;
    private String description;
    private List<ExpertResponse> experts;
    private ProgramStatus programStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;


    public ProgramResponse(Program program, List<ProgramExpert> experts) {
        this.id = program.getId();
        this.name = program.getName();
        this.description = program.getDescription();
        this.experts = experts.stream().map(ExpertResponse::new).toList();
        this.programStatus = program.getStatus();
        this.createdAt = program.getCreatedAt().toString();
    }
}
