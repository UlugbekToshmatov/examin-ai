package ai.examin.admin.model.task.mapper;

import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.task.dto.*;
import ai.examin.admin.model.task.entity.Task;
import ai.examin.core.enums.TaskStatus;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TaskMapper {

    public static List<TaskResponseMin> toResponseList(List<Task> tasks) {
        return tasks.stream().map(TaskResponseMin::new).toList();
    }

    public static Task toEntity(TaskRequest request, Course course) {
        Task task = new Task();
        BeanUtils.copyProperties(request, task, "courseId");
        task.setCourse(course);

        return task;
    }

    public static Task toEntity(TaskRequest request, Course course, UUID mentorId) {
        Task task = new Task();
        BeanUtils.copyProperties(request, task, "courseId", "mentorId");
        task.setCourse(course);
        task.setMentorId(mentorId);

        return task;
    }

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(task);
    }

    public static void update(TaskUpdateRequest request, Task task) {
        BeanUtils.copyProperties(request, task);
    }

    public static void update(TaskUpdateTitleRequest request, Task task) {
        BeanUtils.copyProperties(request, task);
    }

    public static void delete(Task task) {
        task.setStatus(TaskStatus.DELETED);
        task.setDeletedAt(LocalDateTime.now());
    }

}
