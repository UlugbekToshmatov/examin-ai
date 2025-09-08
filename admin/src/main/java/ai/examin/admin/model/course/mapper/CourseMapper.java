package ai.examin.admin.model.course.mapper;

import ai.examin.admin.model.course.dto.*;
import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.entity.CourseMentor;
import ai.examin.admin.model.program.entity.Program;
import ai.examin.core.enums.CourseStatus;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class CourseMapper {

    public static List<CourseResponseMin> toResponseList(List<Course> courses) {
        return courses.stream().map(CourseResponseMin::new).toList();
    }

    public static Course toEntity(CourseRequest request, Program program, Boolean isSupervisor) {
        Course course = new Course();
        BeanUtils.copyProperties(request, course, "programId");
        course.setProgram(program);
        if (isSupervisor)
            course.setStatus(CourseStatus.ACTIVE);      // Course is being created by Supervisor. Thus, ACTIVE by default.
        else
            course.setStatus(CourseStatus.PENDING_APPROVAL);

        return course;
    }

    public static CourseResponse toResponse(Course course, List<CourseMentor> mentors) {
        return new CourseResponse(course, mentors);
    }

    public static void update(CourseUpdateRequest request, Course course) {
        BeanUtils.copyProperties(request, course);
    }

}
