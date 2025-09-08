package ai.examin.admin.model.course.dto;

import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.entity.CourseMentor;
import ai.examin.admin.model.program.dto.ProgramResponseMin;
import ai.examin.core.enums.CourseStatus;
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
public class CourseResponse {
    private Long id;
    private String name;
    private String description;
    private ProgramResponseMin program;
    private List<MentorResponse> mentors;
    private CourseStatus courseStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;


    public CourseResponse(Course course, List<CourseMentor> mentors) {
        this.id = course.getId();
        this.name = course.getName();
        this.description = course.getDescription();
        this.program = new ProgramResponseMin(course.getProgram());
        this.mentors = mentors.stream().map(MentorResponse::new).toList();
        this.courseStatus = course.getStatus();
        this.createdAt = course.getCreatedAt().toString();
    }
}
