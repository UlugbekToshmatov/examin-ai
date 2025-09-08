package ai.examin.admin.model.course.service;

import ai.examin.admin.model.clients.auth.AuthServiceClient;
import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.admin.model.course.dto.CourseResponse;
import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.entity.CourseMentor;
import ai.examin.admin.model.course.repository.CourseMentorRepository;
import ai.examin.admin.model.course.repository.CourseRepository;
import ai.examin.core.enums.CourseStatus;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Role;
import ai.examin.core.enums.SpecialistProgramStatus;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.utils.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static ai.examin.admin.model.course.mapper.CourseMapper.toResponse;
import static ai.examin.admin.model.course.mapper.CourseMentorMapper.toEntity;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseMentorService {
    private final CourseRepository courseRepository;
    private final AuthServiceClient authServiceClient;
    private final CourseMentorRepository courseMentorRepository;
    private final UserContextService userContextService;


    public CourseResponse assignMentor(Long courseId, UUID mentorId) {
        Course course = courseRepository.findByIdAndStatusNot(courseId, CourseStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        UserResponse expert = authServiceClient.getUserById(mentorId);
        if (!expert.getRole().equals(Role.EXPERT))
            throw new ApiException(ResponseStatus.ROLE_MISMATCH_EXCEPTION);

        if (userContextService.getCurrentUserRoles().contains("ROLE_" + Role.SUPERVISOR.name()))
            courseMentorRepository.saveAndFlush(toEntity(course, expert));
//        else
//            // TODO: send approval request to supervisor to assign the mentor

        return toResponse(course, courseMentorRepository.findAllByCourseId(course.getId()));
    }

    public CourseResponse unassignMentor(Long courseId, UUID mentorId) {
        Course course = courseRepository.findByIdAndStatusNot(courseId, CourseStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        if (userContextService.getCurrentUserRoles().contains("ROLE_" + Role.SUPERVISOR.name())) {
            CourseMentor courseMentor = courseMentorRepository
                .findByCourse_IdAndMentorIdAndStatus(course.getId(), mentorId, SpecialistProgramStatus.ASSIGNED)
                .orElseThrow(() -> new ApiException(ResponseStatus.SPECIALIST_NOT_FOUND));

            courseMentor.setStatus(SpecialistProgramStatus.UNASSIGNED);
            courseMentorRepository.saveAndFlush(courseMentor);
        } /*else // TODO: send approval request to supervisor to unassign the mentor*/

        return toResponse(course, courseMentorRepository.findAllByCourseId(course.getId()));
    }
}
