package ai.examin.admin.model.course.dto;

import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.program.dto.ProgramResponseMin;
import ai.examin.core.enums.CourseStatus;
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
public class CourseResponse {
    private Long id;
    private String name;
    private String description;
    private ProgramResponseMin program;
    private UUID expertId;
    private CourseStatus courseStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;


    public CourseResponse(Course course) {
        this.id = course.getId();
        this.name = course.getName();
        this.description = course.getDescription();
        this.program = new ProgramResponseMin(course.getProgram());
        this.expertId = course.getExpertId();
        this.courseStatus = course.getStatus();
        this.createdAt = course.getCreatedAt().toString();
    }
}
