package ai.examin.admin.model.program.service;

import ai.examin.admin.model.clients.auth.AuthClient;
import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.repository.CourseRepository;
import ai.examin.admin.model.program.dto.*;
import ai.examin.admin.model.program.entity.Program;
import ai.examin.admin.model.program.mapper.ProgramMapper;
import ai.examin.admin.model.program.repository.ProgramRepository;
import ai.examin.core.enums.CourseStatus;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Status;
import ai.examin.core.enums.Role;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.utils.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ai.examin.admin.model.program.mapper.ProgramMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {
    private final ProgramRepository programRepository;
    private final CourseRepository courseRepository;
    private final UserContextService userContextService;
    private final AuthClient authClient;


    public List<ProgramResponseMin> getAll() {
        return toResponseList(programRepository.findAllByStatusNot(Status.DELETED));
    }

    public ProgramResponse getById(Long id) {
        return programRepository.findByIdAndStatusNot(id, Status.DELETED)
            .map(ProgramMapper::toResponse)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));
    }

    public ProgramResponse create(ProgramRequest request) {
        Course course = courseRepository.findByIdAndStatusNot(request.courseId(), CourseStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        Program program;

        if (userContextService.getCurrentUserRoles().contains("ROLE_" + Role.SUPERVISOR.name())) {
            if (request.expertId() == null)
                throw new ApiException(ResponseStatus.EXPERT_ID_REQUIRED);

            UserResponse expert = authClient.getUserById(request.expertId());
            if (expert == null)
                throw new ApiException(ResponseStatus.USER_NOT_FOUND);

            program = programRepository.save(toEntity(request, course));
        } else {
            Optional<String> currentUserExternalId = userContextService.getCurrentUserId();
            if (currentUserExternalId.isEmpty()) {
                log.error("User details are not present in the context for expert in ProgramService.create()");
                throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
            }

            UserResponse expert = authClient.getUserByExternalId(currentUserExternalId.get());
            if (expert == null)
                throw new ApiException(ResponseStatus.USER_NOT_FOUND);

            program = programRepository.save(toEntity(request, course, expert.getId()));
        }

        return toResponse(program);
    }

    public ProgramResponse updateById(Long id, ProgramUpdateRequest request) {
        Program program = programRepository.findByIdAndStatusNot(id, Status.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context for supervisor in ProgramService.updateById()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        UserResponse supervisor = authClient.getUserByExternalId(currentUserId.get());
        if (supervisor == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        if (!program.getCourse().getSupervisorId().equals(supervisor.getId()))
            throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

        UserResponse expert = authClient.getUserById(request.expertId());
        if (expert == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        update(request, program);

        return toResponse(programRepository.save(program));
    }

    public ProgramResponse updateDescription(Long id, ProgramDescriptionUpdateRequest request) {
        Program program = programRepository.findByIdAndStatusNot(id, Status.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context in ProgramService.updateDescription()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        UserResponse currentUser = authClient.getUserByExternalId(currentUserId.get());
        if (currentUser == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        if (userContextService.getCurrentUserRoles().contains("ROLE_" + Role.SUPERVISOR.name())) {
            if (!program.getCourse().getSupervisorId().equals(currentUser.getId()))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
        } else {
            if (!program.getExpertId().equals(currentUser.getId()))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
        }

        update(request, program);

        return toResponse(programRepository.save(program));
    }

    public ProgramResponse updateApproval(Long id) {
        Program program = programRepository.findByIdAndStatusNot(id, Status.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context for supervisor in ProgramService.updateApproval()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        UserResponse supervisor = authClient.getUserByExternalId(currentUserId.get());
        if (supervisor == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        if (!program.getCourse().getSupervisorId().equals(supervisor.getId()))
            throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

        program.setApproved(!program.getApproved());
        program.setUpdatedAt(LocalDateTime.now());

        return toResponse(programRepository.save(program));
    }

    public void deleteById(Long id) {
        Program program = programRepository.findByIdAndStatusNot(id, Status.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context in ProgramService.deleteById()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        UserResponse currentUser = authClient.getUserByExternalId(currentUserId.get());
        if (currentUser == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        if (userContextService.getCurrentUserRoles().contains("ROLE_" + Role.SUPERVISOR.name())) {
            if (!program.getCourse().getSupervisorId().equals(currentUser.getId()))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
        } else {
            if (!program.getExpertId().equals(currentUser.getId()))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
        }

        delete(program);
        programRepository.save(program);
    }
}
