package ai.examin.admin.model.program.dto;

import ai.examin.admin.model.program.entity.ProgramExpert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpertResponse {
    private UUID id;
    private String fullName;
    private String email;


    public ExpertResponse(ProgramExpert expert) {
        this.id = expert.getExpertId();
        this.fullName = expert.getExpertFullName();
        this.email = expert.getExpertEmail();
    }
}
