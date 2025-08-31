package ai.examin.admin.model.task.dto;

import ai.examin.admin.model.course.dto.CourseResponseMin;
import ai.examin.admin.model.task.entity.Task;
import ai.examin.core.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private CourseResponseMin course;
    private UUID mentorId;
    private String title;
    private String definition;
    private TaskStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;


    public TaskResponse(Task task) {
        this.id = task.getId();
        this.course = new CourseResponseMin(task.getCourse());
        this.mentorId = task.getMentorId();
        this.title = task.getTitle();
        this.definition = task.getDefinition();
        this.status = task.getStatus();
        this.createdAt = task.getCreatedAt().toString();
    }
}
