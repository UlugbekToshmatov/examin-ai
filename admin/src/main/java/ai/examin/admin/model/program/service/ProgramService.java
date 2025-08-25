package ai.examin.admin.model.program.service;

import ai.examin.admin.model.clients.auth.AuthClient;
import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.repository.CourseRepository;
import ai.examin.admin.model.program.dto.*;
import ai.examin.admin.model.program.entity.Program;
import ai.examin.admin.model.program.repository.ProgramRepository;
import ai.examin.core.enums.CourseStatus;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Status;
import ai.examin.core.enums.UserRole;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.utils.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        Program program = programRepository.findByIdAndStatusNot(id, Status.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        return toResponse(program, program.getCourse());
    }

    public ProgramResponse create(ProgramRequest request) {
        Course course = courseRepository.findByIdAndStatusNot(request.courseId(), CourseStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        Program program;

        if (userContextService.getCurrentUserRoles().contains("ROLE_" + UserRole.SUPERVISOR.name())) {
            if (request.expertId() == null)
                throw new ApiException(ResponseStatus.EXPERT_ID_REQUIRED);

            UserResponse user = authClient.getUserById(request.expertId());

            if (user == null)
                throw new ApiException(ResponseStatus.USER_NOT_FOUND);

            program = programRepository.save(toEntity(request));
        } else {
            Optional<String> currentUserExternalId = userContextService.getCurrentUserId();
            if (currentUserExternalId.isEmpty()) {
                log.error("User details are not present in the context in ProgramService.create()");
                throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
            }

            UserResponse userResponse = authClient.getUserByExternalId(currentUserExternalId.get());

            if (userResponse == null)
                throw new ApiException(ResponseStatus.USER_NOT_FOUND);

            program = programRepository.save(toEntity(request, userResponse.getId()));
        }

        return toResponse(program, course);
    }

    public ProgramResponse updateById(Long id, ProgramUpdateRequest request) {
        Program program = programRepository.findByIdAndStatusNot(id, Status.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        UserResponse user = authClient.getUserById(request.expertId());

        if (user == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        update(request, program);

        return toResponse(programRepository.save(program), program.getCourse());
    }

    public ProgramResponse updateDescription(Long id, ProgramDescriptionUpdateRequest request) {
        Program program = programRepository.findByIdAndStatusNot(id, Status.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        program.setDescription(request.description());

        return toResponse(programRepository.save(program), program.getCourse());
    }

    public ProgramResponse updateApproval(Long id) {
        Program program = programRepository.findByIdAndStatusNot(id, Status.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        program.setApproved(!program.getApproved());

        return toResponse(programRepository.save(program), program.getCourse());
    }

    public void delete(Long id) {
        Program program = programRepository.findByIdAndStatusNot(id, Status.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        program.setStatus(Status.DELETED);
        programRepository.save(program);
    }
}
