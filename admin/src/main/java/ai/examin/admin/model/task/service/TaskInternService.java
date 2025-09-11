package ai.examin.admin.model.task.service;

import ai.examin.admin.model.clients.auth.AuthServiceClient;
import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.entity.CourseMentor;
import ai.examin.admin.model.course.repository.CourseMentorRepository;
import ai.examin.admin.model.program.repository.ProgramExpertRepository;
import ai.examin.admin.model.task.dto.TaskResponse;
import ai.examin.admin.model.task.entity.Task;
import ai.examin.admin.model.task.entity.TaskIntern;
import ai.examin.admin.model.task.mapper.TaskMapper;
import ai.examin.admin.model.task.repository.TaskInternRepository;
import ai.examin.admin.model.task.repository.TaskRepository;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Role;
import ai.examin.core.enums.SpecialistProgramStatus;
import ai.examin.core.enums.TaskStatus;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.security.UserContext;
import ai.examin.core.utils.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ai.examin.admin.model.task.mapper.TaskInternMapper.toEntity;
import static ai.examin.core.enums.SpecialistProgramStatus.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskInternService {
    private final TaskInternRepository taskInternRepository;
    private final TaskRepository taskRepository;
    private final ProgramExpertRepository programExpertRepository;
    private final CourseMentorRepository courseMentorRepository;
    private final AuthServiceClient authServiceClient;
    private final UserContextService userContextService;


    @Transactional
    public TaskResponse assignIntern(Long taskId, UUID internId) {
        Task task = taskRepository.findByIdAndStatusNot(taskId, TaskStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));

        UserContext currentUser = getCurrentUser();
        Course course = task.getCourse();

        if (currentUser.getRoles().contains("ROLE_" + Role.SUPERVISOR.name())) {
            assign(task, internId);
        } else if (currentUser.getRoles().contains("ROLE_" + Role.EXPERT.name())) {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), currentUser.getId(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            assign(task, internId);
        } else {
            // Check if mentor is assigned to the course
            if (!courseMentorRepository.existsByCourseAndMentorIdAndStatus(course, currentUser.getId(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            UserResponse intern = getIntern(internId);
            taskInternRepository.saveAndFlush(toEntity(task, intern, TRUE));
            // TODO: send approval request to expert to assign the intern
        }

        List<CourseMentor> mentors = courseMentorRepository.findAllByCourseId(course.getId());
        List<TaskIntern> interns = taskInternRepository.findAllByTaskId(task.getId());

        return TaskMapper.toResponse(task, course, mentors, interns);
    }

    @Transactional
    public TaskResponse unassignIntern(Long taskId, UUID internId) {
        Task task = taskRepository.findByIdAndStatusNot(taskId, TaskStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));

        UserContext currentUser = getCurrentUser();
        Course course = task.getCourse();

        if (currentUser.getRoles().contains("ROLE_" + Role.SUPERVISOR.name())) {
            TaskIntern taskIntern = taskInternRepository.findByTask_IdAndInternId(task.getId(), internId)
                .orElseThrow(() -> new ApiException(ResponseStatus.SPECIALIST_NOT_FOUND));

            if (taskIntern.getStatus().equals(UNASSIGNED))
                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_UNASSIGNED);

            taskIntern.setStatus(SpecialistProgramStatus.UNASSIGNED);
            taskInternRepository.saveAndFlush(taskIntern);
        } else if (currentUser.getRoles().contains("ROLE_" + Role.EXPERT.name())) {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), currentUser.getId(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            TaskIntern taskIntern = taskInternRepository.findByTask_IdAndInternId(task.getId(), internId)
                .orElseThrow(() -> new ApiException(ResponseStatus.SPECIALIST_NOT_FOUND));

            if (taskIntern.getStatus().equals(UNASSIGNED))
                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_UNASSIGNED);

            taskIntern.setStatus(SpecialistProgramStatus.UNASSIGNED);
            taskInternRepository.saveAndFlush(taskIntern);
        } else {
            // Check if mentor is assigned to the course
            if (!courseMentorRepository.existsByCourseAndMentorIdAndStatus(course, currentUser.getId(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            TaskIntern taskIntern = taskInternRepository.findByTask_IdAndInternId(task.getId(), internId)
                .orElseThrow(() -> new ApiException(ResponseStatus.SPECIALIST_NOT_FOUND));

            if (taskIntern.getStatus().equals(UNASSIGNED))
                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_UNASSIGNED);

            // TODO: send approval request to expert to unassign the intern
        }

        List<CourseMentor> mentors = courseMentorRepository.findAllByCourseId(course.getId());
        List<TaskIntern> interns = taskInternRepository.findAllByTaskId(task.getId());

        return TaskMapper.toResponse(task, course, mentors, interns);
    }

    private void assign(Task task, UUID internId) {
        Optional<TaskIntern> optionalTaskIntern = taskInternRepository.findByTask_IdAndInternId(task.getId(), internId);
        if (optionalTaskIntern.isEmpty()) {
            UserResponse intern = getIntern(internId);
            taskInternRepository.saveAndFlush(toEntity(task, intern, FALSE));
        } else {
            TaskIntern taskIntern = optionalTaskIntern.get();
            if (taskIntern.getStatus().equals(ASSIGNED))
                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_ASSIGNED);

            taskIntern.setStatus(SpecialistProgramStatus.ASSIGNED);
            taskInternRepository.saveAndFlush(taskIntern);
        }
    }

    private UserContext getCurrentUser() {
        Optional<UserContext> currentUserContext = userContextService.getCurrentUserContext();
        if (currentUserContext.isEmpty()) {
            log.error("User details are not present in the context in TaskInternService.getCurrentUser()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        return currentUserContext.get();
    }

    private UserResponse getIntern(UUID internId) {
        UserResponse intern = authServiceClient.getUserById(internId);
        if (!intern.getRole().equals(Role.INTERN))
            throw new ApiException(ResponseStatus.ROLE_MISMATCH_EXCEPTION);

        return intern;
    }
}
