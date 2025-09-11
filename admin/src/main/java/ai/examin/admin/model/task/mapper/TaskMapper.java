package ai.examin.admin.model.task.mapper;

import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.entity.CourseMentor;
import ai.examin.admin.model.task.dto.*;
import ai.examin.admin.model.task.entity.Task;
import ai.examin.admin.model.task.entity.TaskIntern;
import ai.examin.core.enums.TaskStatus;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class TaskMapper {

    public static List<TaskResponseMin> toResponseList(List<Task> tasks) {
        return tasks.stream().map(TaskResponseMin::new).toList();
    }

    public static Task toEntity(TaskRequest request, Course course, Boolean isMentor) {
        Task task = new Task();
        BeanUtils.copyProperties(request, task, "courseId");
        task.setCourse(course);
        if (isMentor)
            task.setStatus(TaskStatus.PENDING_APPROVAL);
        else
            task.setStatus(TaskStatus.ACTIVE);

        return task;
    }

    public static TaskResponse toResponse(Task task, Course course, List<CourseMentor> mentors, List<TaskIntern> interns) {
        return new TaskResponse(task, course, mentors, interns);
    }

    public static void update(TaskUpdateRequest request, Task task) {
        BeanUtils.copyProperties(request, task);
    }

}
