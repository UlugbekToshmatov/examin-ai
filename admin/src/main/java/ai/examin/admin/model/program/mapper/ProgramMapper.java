package ai.examin.admin.model.program.mapper;

import ai.examin.admin.model.program.dto.ProgramRequest;
import ai.examin.admin.model.program.dto.ProgramResponse;
import ai.examin.admin.model.program.dto.ProgramResponseMin;
import ai.examin.admin.model.program.dto.ProgramUpdateRequest;
import ai.examin.admin.model.program.entity.Program;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.UUID;

public class ProgramMapper {

    public static Program toEntity(ProgramRequest request, UUID supervisorId) {
        Program program = new Program();
        program.setName(request.name());
        program.setSupervisorId(supervisorId);

        return program;
    }

    public static ProgramResponse toResponse(Program program) {
        ProgramResponse programResponse = new ProgramResponse();
        BeanUtils.copyProperties(program, programResponse);

        return programResponse;
    }

    public static List<ProgramResponseMin> toResponseList(List<Program> programs) {
        return programs.stream().map(ProgramResponseMin::new).toList();
    }

    public static void update(ProgramUpdateRequest request, Program program) {
        BeanUtils.copyProperties(request, program);
    }

}
