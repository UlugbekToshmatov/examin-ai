package ai.examin.admin.model.course.mapper;

import ai.examin.admin.model.course.dto.CourseRequest;
import ai.examin.admin.model.course.dto.CourseResponse;
import ai.examin.admin.model.course.dto.CourseResponseMin;
import ai.examin.admin.model.course.dto.CourseUpdateRequest;
import ai.examin.admin.model.course.entity.Course;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class CourseMapper {

    public static Course toEntity(CourseRequest request) {
        Course course = new Course();
        BeanUtils.copyProperties(request, course);

        return course;
    }

    public static CourseResponse toResponse(Course course) {
        CourseResponse courseResponse = new CourseResponse();
        BeanUtils.copyProperties(course, courseResponse);

        return courseResponse;
    }

    public static List<CourseResponseMin> toResponseList(List<Course> courses) {
        return courses.stream().map(CourseResponseMin::new).toList();
    }

    public static void update(CourseUpdateRequest request, Course course) {
        BeanUtils.copyProperties(request, course);
    }
}
