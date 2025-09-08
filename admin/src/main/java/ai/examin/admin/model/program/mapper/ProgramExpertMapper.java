package ai.examin.admin.model.program.mapper;

import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.admin.model.program.entity.Program;
import ai.examin.admin.model.program.entity.ProgramExpert;

public class ProgramExpertMapper {

    public static ProgramExpert toEntity(Program program, UserResponse expert) {
        ProgramExpert programExpert = new ProgramExpert();
        programExpert.setProgram(program);
        programExpert.setExpertId(expert.getId());
        programExpert.setExpertFullName(expert.getFirstName() + " " + expert.getLastName());
        programExpert.setExpertEmail(expert.getEmail());

        return programExpert;
    }

}
