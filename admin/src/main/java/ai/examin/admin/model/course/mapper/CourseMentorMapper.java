package ai.examin.admin.model.course.mapper;

import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.entity.CourseMentor;

public class CourseMentorMapper {

    public static CourseMentor toEntity(Course course, UserResponse mentor) {
        CourseMentor courseMentor = new CourseMentor();
        courseMentor.setCourse(course);
        courseMentor.setMentorId(mentor.getId());
        courseMentor.setMentorFullName(mentor.getFirstName() + " " + mentor.getLastName());
        courseMentor.setMentorEmail(mentor.getEmail());

        return courseMentor;
    }

}
