package ai.examin.admin.model.program.mapper;

import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.program.dto.ProgramRequest;
import ai.examin.admin.model.program.dto.ProgramResponse;
import ai.examin.admin.model.program.dto.ProgramResponseMin;
import ai.examin.admin.model.program.dto.ProgramUpdateRequest;
import ai.examin.admin.model.program.entity.Program;
import org.springframework.beans.BeanUtils;

import java.util.List;

import static java.lang.Boolean.TRUE;

public class ProgramMapper {

    public static Program toEntity(ProgramRequest request) {
        Program program = new Program();
        BeanUtils.copyProperties(request, program);
        program.setApproved(TRUE);      // Program is being created by Supervisor. Thus, approved by default.

        return program;
    }

    public static Program toEntity(ProgramRequest request, Long userId) {
        Program program = new Program();
        BeanUtils.copyProperties(request, program);
        program.setExpertId(userId);

        return program;
    }

    public static ProgramResponse toResponse(Program program, Course course) {
        return new ProgramResponse(program, course);
    }

    public static void update(ProgramUpdateRequest request, Program program) {
        BeanUtils.copyProperties(request, program);
    }

    public static List<ProgramResponseMin> toResponseList(List<Program> programs) {
        return programs.stream().map(ProgramResponseMin::new).toList();
    }

}
