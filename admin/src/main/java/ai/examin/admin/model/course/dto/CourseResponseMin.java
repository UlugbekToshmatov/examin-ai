package ai.examin.admin.model.course.dto;

import ai.examin.admin.model.course.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseMin {
    private Long id;
    private String name;


    public CourseResponseMin(Course course) {
        this.id = course.getId();
        this.name = course.getName();
    }
}
