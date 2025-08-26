package ai.examin.admin.model.program.mapper;

import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.program.dto.*;
import ai.examin.admin.model.program.entity.Program;
import ai.examin.core.enums.Status;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Boolean.TRUE;

public class ProgramMapper {

    public static List<ProgramResponseMin> toResponseList(List<Program> programs) {
        return programs.stream().map(ProgramResponseMin::new).toList();
    }

    public static Program toEntity(ProgramRequest request, Course course) {
        Program program = new Program();
        BeanUtils.copyProperties(request, program, "courseId");
        program.setCourse(course);
        program.setApproved(TRUE);      // Program is being created by Supervisor. Thus, approved by default.

        return program;
    }

    public static Program toEntity(ProgramRequest request, Course course, Long userId) {
        Program program = new Program();
        BeanUtils.copyProperties(request, program, "courseId", "expertId");
        program.setCourse(course);
        program.setExpertId(userId);

        return program;
    }

    public static ProgramResponse toResponse(Program program) {
        return new ProgramResponse(program);
    }

    public static void update(ProgramUpdateRequest request, Program program) {
        BeanUtils.copyProperties(request, program);
        program.setUpdatedAt(LocalDateTime.now());
    }

    public static void update(ProgramDescriptionUpdateRequest request, Program program) {
        program.setDescription(request.description());
        program.setUpdatedAt(LocalDateTime.now());
    }

    public static void delete(Program program) {
        program.setStatus(Status.DELETED);
        program.setDeletedAt(LocalDateTime.now());
    }

}
