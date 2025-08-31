package ai.examin.admin.model.course.service;

import ai.examin.admin.model.clients.auth.AuthClient;
import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.admin.model.course.dto.*;
import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.mapper.CourseMapper;
import ai.examin.admin.model.course.repository.CourseRepository;
import ai.examin.admin.model.program.entity.Program;
import ai.examin.admin.model.program.repository.ProgramRepository;
import ai.examin.core.enums.CourseStatus;
import ai.examin.core.enums.ProgramStatus;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Role;
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


@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final ProgramRepository programRepository;
    private final UserContextService userContextService;
    private final AuthClient authClient;


    public List<CourseResponseMin> getAll() {
        return toResponseList(courseRepository.findAllByStatusNot(CourseStatus.DELETED));
    }

    public CourseResponse getById(Long id) {
        return courseRepository.findByIdAndStatusNot(id, CourseStatus.DELETED)
            .map(CourseMapper::toResponse)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));
    }

    public CourseResponse create(CourseRequest request) {
        Program program = programRepository.findByIdAndStatusNot(request.programId(), ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        Course course;

        if (userContextService.getCurrentUserRoles().contains("ROLE_" + Role.SUPERVISOR.name())) {
            if (request.expertId() == null)
                throw new ApiException(ResponseStatus.EXPERT_ID_REQUIRED);

            UserResponse expert = authClient.getUserByIdInternal(request.expertId());
            if (expert == null)
                throw new ApiException(ResponseStatus.USER_NOT_FOUND);

            course = courseRepository.save(toEntity(request, program));
        } else {
            Optional<String> currentUserId = userContextService.getCurrentUserId();
            if (currentUserId.isEmpty()) {
                log.error("User details are not present in the context for expert in CourseService.create()");
                throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
            }

            // Verify expert exists in local DB
            UserResponse expert = authClient.getUserByIdInternal(UUID.fromString(currentUserId.get()));
            if (expert == null)
                throw new ApiException(ResponseStatus.USER_NOT_FOUND);

            course = courseRepository.save(toEntity(request, program, expert.getId()));
        }

        return toResponse(course);
    }

    public CourseResponse updateById(Long id, CourseUpdateRequest request) {
        Course course = courseRepository.findByIdAndStatusNot(id, CourseStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        if (courseRepository.existsByProgramAndNameAndStatusNot(course.getProgram(), request.name(), CourseStatus.DELETED))
            throw new ApiException(ResponseStatus.COURSE_ALREADY_EXISTS);

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context for supervisor in CourseService.updateById()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        // Verify supervisor exists in local DB
        UserResponse supervisor = authClient.getUserByIdInternal(UUID.fromString(currentUserId.get()));
        if (supervisor == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        if (!course.getProgram().getSupervisorId().equals(supervisor.getId()))
            throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

        // Verify new expert exists in local DB
        UserResponse expert = authClient.getUserByIdInternal(request.expertId());
        if (expert == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        update(request, course);

        return toResponse(courseRepository.save(course));
    }

    public CourseResponse updateName(Long id, CourseUpdateNameRequest request) {
        Course course = courseRepository.findByIdAndStatusNot(id, CourseStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context in CourseService.updateName()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        UserResponse currentUser = authClient.getUserByIdInternal(UUID.fromString(currentUserId.get()));
        if (currentUser == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        // Verify if current user's role in local DB matches with the one in Keycloak
        if (!userContextService.getCurrentUserRoles().contains("ROLE_" + currentUser.getRole().name()))
            throw new ApiException(ResponseStatus.ROLE_MISMATCH_EXCEPTION);

        if (currentUser.getRole().equals(Role.SUPERVISOR)) {
            if (!course.getProgram().getSupervisorId().equals(currentUser.getId()))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
        } else {
            if (!course.getExpertId().equals(currentUser.getId()))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
        }

        update(request, course);

        return toResponse(courseRepository.save(course));
    }

    public CourseResponse approveCourse(Long id) {
        Course course = courseRepository.findByIdAndStatusNot(id, CourseStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context for supervisor in CourseService.updateApproval()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        // Verify supervisor exists in local DB
        UserResponse supervisor = authClient.getUserByIdInternal(UUID.fromString(currentUserId.get()));
        if (supervisor == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        if (!course.getProgram().getSupervisorId().equals(supervisor.getId()))
            throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

        course.setStatus(CourseStatus.APPROVED);

        return toResponse(courseRepository.save(course));
    }

    @Transactional
    public void deleteById(Long id) {
        Course course = courseRepository.findByIdAndStatusNot(id, CourseStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context in CourseService.deleteById()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        // Verify current user exists in local DB
        UserResponse currentUser = authClient.getUserByIdInternal(UUID.fromString(currentUserId.get()));
        if (currentUser == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        // Verify if current user's role in local DB matches with the one in Keycloak
        if (!userContextService.getCurrentUserRoles().contains("ROLE_" + currentUser.getRole().name()))
            throw new ApiException(ResponseStatus.ROLE_MISMATCH_EXCEPTION);

        if (currentUser.getRole().equals(Role.SUPERVISOR)) {
            if (!course.getProgram().getSupervisorId().equals(currentUser.getId()))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
        } else {
            if (!course.getExpertId().equals(currentUser.getId()))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
        }

        course.softDelete(currentUser.getId());
        courseRepository.save(course);

        // Delete all tasks associated with the course
        // taskService.deleteAllByCourseId(id);
    }
}
