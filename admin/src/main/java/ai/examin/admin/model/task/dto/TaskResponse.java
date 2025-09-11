package ai.examin.admin.model.task.dto;

import ai.examin.admin.model.course.dto.CourseResponseMin;
import ai.examin.admin.model.course.dto.MentorResponse;
import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.entity.CourseMentor;
import ai.examin.admin.model.task.entity.Task;
import ai.examin.admin.model.task.entity.TaskIntern;
import ai.examin.core.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private CourseResponseMin course;
    private List<MentorResponse> mentors;
    private List<InternResponse> interns;
    private String title;
    private String definition;
    private TaskStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;


    public TaskResponse(Task task, Course course, List<CourseMentor> mentors, List<TaskIntern> interns) {
        this.id = task.getId();
        this.course = new CourseResponseMin(course);
        this.mentors = mentors.stream().map(MentorResponse::new).toList();
        this.interns = interns.stream().map(InternResponse::new).toList();
        this.title = task.getTitle();
        this.definition = task.getDefinition();
        this.status = task.getStatus();
        this.createdAt = task.getCreatedAt().toString();
    }
}
