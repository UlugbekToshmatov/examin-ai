package ai.examin.admin.model.course.service;

import ai.examin.admin.model.course.dto.CourseRequest;
import ai.examin.admin.model.course.dto.CourseResponse;
import ai.examin.admin.model.course.dto.CourseResponseMin;
import ai.examin.admin.model.course.dto.CourseUpdateRequest;
import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.entity.CourseMentor;
import ai.examin.admin.model.course.repository.CourseMentorRepository;
import ai.examin.admin.model.course.repository.CourseRepository;
import ai.examin.admin.model.program.entity.Program;
import ai.examin.admin.model.program.repository.ProgramExpertRepository;
import ai.examin.admin.model.program.repository.ProgramRepository;
import ai.examin.core.enums.*;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.utils.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ai.examin.admin.model.course.mapper.CourseMapper.*;
import static ai.examin.core.enums.CourseStatus.DELETED;
import static ai.examin.core.enums.SpecialistProgramStatus.ASSIGNED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMentorRepository courseMentorRepository;
    private final ProgramRepository programRepository;
    private final ProgramExpertRepository programExpertRepository;
    private final UserContextService userContextService;


    public List<CourseResponseMin> getAll() {
        return toResponseList(courseRepository.findAllByStatusNot(DELETED));
    }

    public List<CourseResponseMin> getAllByProgram(Long programId) {
        return toResponseList(courseRepository.findAllByProgram_IdAndStatusNot(programId, DELETED));
    }

    public CourseResponse getById(Long id) {
        Course course = courseRepository.findByIdAndStatusNot(id, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        List<CourseMentor> mentors = courseMentorRepository.findAllByCourseId(course.getId());

        return toResponse(course, mentors);
    }

    public CourseResponse create(CourseRequest request) {
        Program program = programRepository.findByIdAndStatusNot(request.programId(), ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        if (courseRepository.existsByProgramAndNameAndStatusNot(program, request.name(), DELETED))
            throw new ApiException(ResponseStatus.COURSE_ALREADY_EXISTS);

        Course course;
        if (userContextService.getCurrentUserRoles().contains(Role.SUPERVISOR.name()))
            course = courseRepository.save(toEntity(request, program, TRUE));
        else {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(program, getCurrentUserId(), ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            course = courseRepository.save(toEntity(request, program, FALSE));
            // TODO: send approval request to supervisor to create course
        }

        return toResponse(course, List.of());
    }

    public CourseResponse updateById(Long id, CourseUpdateRequest request) {
        Course course = courseRepository.findByIdAndStatusNot(id, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        if (courseRepository.existsByProgramAndNameAndStatusNot(course.getProgram(), request.name(), DELETED))
            throw new ApiException(ResponseStatus.COURSE_ALREADY_EXISTS);

        if (userContextService.getCurrentUserRoles().contains(Role.SUPERVISOR.name())) {
            update(request, course);
            courseRepository.saveAndFlush(course);
        } else {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), getCurrentUserId(), ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            // TODO: send approval request to supervisor to update course details
        }

        List<CourseMentor> mentors = courseMentorRepository.findAllByCourseId(course.getId());
        return toResponse(course, mentors);
    }

//    public CourseResponse approveCourse(Long id) {  // TODO: update
//        Course course = courseRepository.findByIdAndStatusNot(id, CourseStatus.DELETED)
//            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));
//
//        Optional<String> currentUserId = userContextService.getCurrentUserId();
//        if (currentUserId.isEmpty()) {
//            log.error("User details are not present in the context for supervisor in CourseService.updateApproval()");
//            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
//        }
//
//        // Verify supervisor exists in local DB
//        UserResponse supervisor = authClient.getUserByIdInternal(UUID.fromString(currentUserId.get()));
//        if (supervisor == null)
//            throw new ApiException(ResponseStatus.USER_NOT_FOUND);
//
//        if (!course.getProgram().getExpertId().equals(supervisor.getId()))
//            throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
//
//        course.setStatus(CourseStatus.APPROVED);
//
//        return toResponse(courseRepository.save(course));
//    }

    @Transactional
    public void deleteById(Long id) {
        Course course = courseRepository.findByIdAndStatusNot(id, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        UUID currentUserId = getCurrentUserId();
        if (userContextService.getCurrentUserRoles().contains(Role.SUPERVISOR.name())) {
            course.softDelete(currentUserId);
            courseRepository.saveAndFlush(course);
        } else {
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), currentUserId, ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            // TODO: send approval request to supervisor to delete course
        }

        // Delete all tasks associated with the course
        // taskService.deleteAllByCourseId(id);
    }

    private UUID getCurrentUserId() {
        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context in CourseService.getCurrentUserId()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        return UUID.fromString(currentUserId.get());
    }
}
