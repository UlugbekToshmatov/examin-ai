package ai.examin.admin.model.task.service;

import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.entity.CourseMentor;
import ai.examin.admin.model.course.mapper.CourseMentorMapper;
import ai.examin.admin.model.course.repository.CourseMentorRepository;
import ai.examin.admin.model.course.repository.CourseRepository;
import ai.examin.admin.model.program.repository.ProgramExpertRepository;
import ai.examin.admin.model.task.dto.TaskRequest;
import ai.examin.admin.model.task.dto.TaskResponse;
import ai.examin.admin.model.task.dto.TaskResponseMin;
import ai.examin.admin.model.task.dto.TaskUpdateRequest;
import ai.examin.admin.model.task.entity.Task;
import ai.examin.admin.model.task.entity.TaskIntern;
import ai.examin.admin.model.task.repository.TaskInternRepository;
import ai.examin.admin.model.task.repository.TaskRepository;
import ai.examin.core.enums.*;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.security.UserContext;
import ai.examin.core.utils.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ai.examin.admin.model.task.mapper.TaskMapper.*;
import static ai.examin.core.enums.SpecialistProgramStatus.ASSIGNED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskInternRepository taskInternRepository;
    private final CourseRepository courseRepository;
    private final CourseMentorRepository courseMentorRepository;
    private final ProgramExpertRepository programExpertRepository;
    private final UserContextService userContextService;


    public List<TaskResponseMin> getAll() {
        return toResponseList(taskRepository.findAllByStatusNot(TaskStatus.DELETED));
    }

    public List<TaskResponseMin> getAllByCourseId(Long courseId) {
        return toResponseList(taskRepository.findAllByCourse_IdAndStatusNot(courseId, TaskStatus.DELETED));
    }

    public TaskResponse getById(Long id) {
        Task task = taskRepository.findByIdAndStatusNot(id, TaskStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));

        Course course = task.getCourse();
        List<CourseMentor> mentors = courseMentorRepository.findAllByCourseId(course.getId());
        List<TaskIntern> interns = taskInternRepository.findAllByTaskId(task.getId());

        return toResponse(task, course, mentors, interns);
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        Course course = courseRepository.findByIdAndStatusNot(request.courseId(), CourseStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        if (taskRepository.existsByCourse_IdAndTitleAndStatusNot(request.courseId(), request.title(), TaskStatus.DELETED))
            throw new ApiException(ResponseStatus.TASK_ALREADY_EXISTS);

        UserContext currentUser = getCurrentUser();

        if (currentUser.getRoles().contains("ROLE_" + Role.SUPERVISOR.name())) {
            Task task = taskRepository.save(toEntity(request, course, FALSE));
            return toResponse(task, course, List.of(), List.of());
        } else if (currentUser.getRoles().contains("ROLE_" + Role.EXPERT.name())) {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), currentUser.getId(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            Task task = taskRepository.save(toEntity(request, course, FALSE));
            return toResponse(task, course, List.of(), List.of());
        } else {
            // Check if mentor is assigned to the course
            if (!courseMentorRepository.existsByCourseAndMentorIdAndStatus(course, currentUser.getId(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            Task task = taskRepository.save(toEntity(request, course, TRUE));
            CourseMentor courseMentor = courseMentorRepository.save(CourseMentorMapper.toEntity(course, currentUser));
            // TODO: send approval request to expert to create the task
            return toResponse(task, course, List.of(courseMentor), List.of());
        }
    }

    public TaskResponse updateById(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findByIdAndStatusNot(id, TaskStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));

        if (taskRepository.existsByCourse_IdAndTitleAndStatusNot(task.getCourse().getId(), request.title(), TaskStatus.DELETED))
            throw new ApiException(ResponseStatus.TASK_ALREADY_EXISTS);

        UserContext currentUser = getCurrentUser();
        Course course = task.getCourse();

        if (currentUser.getRoles().contains("ROLE_" + Role.SUPERVISOR.name())) {
            update(request, task);
            taskRepository.save(task);
        } else if (currentUser.getRoles().contains("ROLE_" + Role.EXPERT.name())) {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), currentUser.getId(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            update(request, task);
            taskRepository.save(task);
        } else {
            // Check if mentor is assigned to the course
            if (!courseMentorRepository.existsByCourseAndMentorIdAndStatus(course, currentUser.getId(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            // TODO: send approval request to expert to update the task
        }

        List<CourseMentor> mentors = courseMentorRepository.findAllByCourseId(course.getId());
        List<TaskIntern> interns = taskInternRepository.findAllByTaskId(task.getId());

        return toResponse(task, course, mentors, interns);
    }

    public void deleteById(Long id) {
        Task task = taskRepository.findByIdAndStatusNot(id, TaskStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));

        UserContext currentUser = getCurrentUser();
        Course course = task.getCourse();

        if (currentUser.getRoles().contains("ROLE_" + Role.EXPERT.name())) {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), currentUser.getId(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
        } else if (currentUser.getRoles().contains("ROLE_" + Role.MENTOR.name())) {
            // Check if mentor is assigned to the course
            if (!courseMentorRepository.existsByCourseAndMentorIdAndStatus(course, currentUser.getId(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            // TODO: send approval request to expert to delete the task
            return;
        }

        task.softDelete(currentUser.getId());
        taskRepository.save(task);
    }

    private UserContext getCurrentUser() {
        Optional<UserContext> currentUserContext = userContextService.getCurrentUserContext();
        if (currentUserContext.isEmpty()) {
            log.error("User details are not present in the context in TaskService.getCurrentUser()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        return currentUserContext.get();
    }
}
