package ai.examin.admin.model.program.dto;

import ai.examin.admin.model.course.dto.CourseResponseMin;
import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.program.entity.Program;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramResponse {
    private Long id;
    private CourseResponseMin course;
    private Long expertId;
    private String description;
    private Boolean approved;


    public ProgramResponse(Program program, Course course) {
        this.id = program.getId();
        this.course = new CourseResponseMin(course);
        this.expertId = program.getExpertId();
        this.description = program.getDescription();
        this.approved = program.getApproved();
    }
}
