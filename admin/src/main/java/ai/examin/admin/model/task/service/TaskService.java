package ai.examin.admin.model.task.service;

import ai.examin.admin.model.clients.auth.AuthClient;
import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.admin.model.program.entity.Program;
import ai.examin.admin.model.program.repository.ProgramRepository;
import ai.examin.admin.model.task.dto.*;
import ai.examin.admin.model.task.entity.Task;
import ai.examin.admin.model.task.repository.TaskInternRepository;
import ai.examin.admin.model.task.repository.TaskRepository;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Status;
import ai.examin.core.enums.TaskStatus;
import ai.examin.core.enums.UserRole;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.utils.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ai.examin.admin.model.task.mapper.TaskMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskInternRepository taskInternRepository;
    private final ProgramRepository programRepository;
    private final UserContextService userContextService;
    private final AuthClient authClient;


    public List<TaskResponseMin> getAll() {
        return toResponseList(taskRepository.findAllByStatusNot(TaskStatus.DELETED));
    }

    public List<TaskResponseMin> getAllByProgramId(Long programId) {
        return toResponseList(taskRepository.findAllByProgramIdAndStatusNot(programId, TaskStatus.DELETED));
    }

    public TaskResponse getById(Long id) {
        return toResponse(taskRepository.findByIdAndStatusNot(id, TaskStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND)));
    }

    public TaskResponse create(TaskRequest request) {
        Program program = programRepository.findByIdAndStatusNot(request.programId(), Status.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        if (taskRepository.existsByProgram_IdAndTitleAndStatusNot(request.programId(), request.title(), TaskStatus.DELETED))
            throw new ApiException(ResponseStatus.TASK_ALREADY_EXISTS);

        Task task;

        if (userContextService.getCurrentUserRoles().contains("ROLE_" + UserRole.SUPERVISOR.name())
        || userContextService.getCurrentUserRoles().contains("ROLE_" + UserRole.EXPERT.name())) {
            if (request.mentorId() == null)
                throw new ApiException(ResponseStatus.MENTOR_ID_REQUIRED);

            UserResponse mentor = authClient.getUserById(request.mentorId());

            if (mentor == null)
                throw new ApiException(ResponseStatus.USER_NOT_FOUND);

            if (!mentor.getRole().equals(UserRole.MENTOR))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            task = taskRepository.save(toEntity(request, program));
        } else {
            Optional<String> currentUserId = userContextService.getCurrentUserId();
            if (currentUserId.isEmpty()) {
                log.error("User details are not present in the context for mentor in TaskService.create()");
                throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
            }

            UserResponse mentor = authClient.getUserByExternalId(currentUserId.get());
            if (mentor == null)
                throw new ApiException(ResponseStatus.USER_NOT_FOUND);

            task = taskRepository.save(toEntity(request, program, mentor.getId()));
        }

        return toResponse(task);
    }

    public TaskResponse updateById(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findByIdAndStatusNot(id, TaskStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));

        if (taskRepository.existsByProgram_IdAndTitleAndStatusNot(task.getProgram().getId(), request.title(), TaskStatus.DELETED))
            throw new ApiException(ResponseStatus.TASK_ALREADY_EXISTS);

        UserResponse mentor = authClient.getUserById(request.mentorId());
        if (mentor == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        update(request, task);

        return toResponse(taskRepository.save(task));
    }

    public TaskResponse updateTitle(Long id, TaskUpdateTitleRequest request) {
        Task task = taskRepository.findByIdAndStatusNot(id, TaskStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));

        if (taskRepository.existsByProgram_IdAndTitleAndStatusNot(task.getProgram().getId(), request.title(), TaskStatus.DELETED))
            throw new ApiException(ResponseStatus.TASK_ALREADY_EXISTS);

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context for mentor in TaskService.updateTitle()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        UserResponse mentor = authClient.getUserByExternalId(currentUserId.get());
        if (mentor == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        if (!Objects.equals(task.getMentorId(), mentor.getId()))
            throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

        update(request, task);

        return toResponse(taskRepository.save(task));
    }

    public void deleteById(Long id) {
        Task task = taskRepository.findByIdAndStatusNot(id, TaskStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context in TaskService.deleteById()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        UserResponse currentUser = authClient.getUserByExternalId(currentUserId.get());
        if (currentUser == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        if (userContextService.getCurrentUserRoles().contains("ROLE_" + UserRole.SUPERVISOR.name())) {
            if (!task.getProgram().getCourse().getSupervisorId().equals(currentUser.getId()))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
        } else if (userContextService.getCurrentUserRoles().contains("ROLE_" + UserRole.EXPERT.name())) {
            if (!task.getProgram().getExpertId().equals(currentUser.getId()))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
        } else {
            if (!task.getMentorId().equals(currentUser.getId()))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
        }

        delete(task);
        taskRepository.save(task);
    }
}
