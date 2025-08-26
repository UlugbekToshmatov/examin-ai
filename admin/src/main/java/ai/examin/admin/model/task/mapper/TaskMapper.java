package ai.examin.admin.model.task.mapper;

import ai.examin.admin.model.program.entity.Program;
import ai.examin.admin.model.task.dto.*;
import ai.examin.admin.model.task.entity.Task;
import ai.examin.core.enums.TaskStatus;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

public class TaskMapper {

    public static List<TaskResponseMin> toResponseList(List<Task> tasks) {
        return tasks.stream().map(TaskResponseMin::new).toList();
    }

    public static Task toEntity(TaskRequest request, Program program) {
        Task task = new Task();
        BeanUtils.copyProperties(request, task, "programId");
        task.setProgram(program);

        return task;
    }

    public static Task toEntity(TaskRequest request, Program program, Long mentorId) {
        Task task = new Task();
        BeanUtils.copyProperties(request, task, "programId", "mentorId");
        task.setProgram(program);
        task.setMentorId(mentorId);

        return task;
    }

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(task);
    }

    public static void update(TaskUpdateRequest request, Task task) {
        BeanUtils.copyProperties(request, task);
        task.setUpdatedAt(LocalDateTime.now());
    }

    public static void update(TaskUpdateTitleRequest request, Task task) {
        BeanUtils.copyProperties(request, task);
        task.setUpdatedAt(LocalDateTime.now());
    }

    public static void delete(Task task) {
        task.setStatus(TaskStatus.DELETED);
        task.setDeletedAt(LocalDateTime.now());
    }

}
