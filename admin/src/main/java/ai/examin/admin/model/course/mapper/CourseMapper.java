package ai.examin.admin.model.course.mapper;

import ai.examin.admin.model.course.dto.*;
import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.program.entity.Program;
import ai.examin.core.enums.CourseStatus;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.UUID;

public class CourseMapper {

    public static List<CourseResponseMin> toResponseList(List<Course> courses) {
        return courses.stream().map(CourseResponseMin::new).toList();
    }

    public static Course toEntity(CourseRequest request, Program program) {
        Course course = new Course();
        BeanUtils.copyProperties(request, course, "courseId");
        course.setProgram(program);
        course.setStatus(CourseStatus.APPROVED);      // Course is being created by Supervisor. Thus, APPROVED by default.

        return course;
    }

    public static Course toEntity(CourseRequest request, Program program, UUID expertId) {
        Course course = new Course();
        BeanUtils.copyProperties(request, course, "courseId", "expertId");
        course.setProgram(program);
        course.setExpertId(expertId);

        return course;
    }

    public static CourseResponse toResponse(Course course) {
        return new CourseResponse(course);
    }

    public static void update(CourseUpdateRequest request, Course course) {
        BeanUtils.copyProperties(request, course);
    }

    public static void update(CourseUpdateNameRequest request, Course course) {
        BeanUtils.copyProperties(request, course);
    }

}
