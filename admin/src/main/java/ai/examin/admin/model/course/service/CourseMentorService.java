package ai.examin.admin.model.course.service;

import ai.examin.admin.model.clients.auth.AuthServiceClient;
import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.admin.model.course.dto.CourseResponse;
import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.entity.CourseMentor;
import ai.examin.admin.model.course.repository.CourseMentorRepository;
import ai.examin.admin.model.course.repository.CourseRepository;
import ai.examin.admin.model.program.repository.ProgramExpertRepository;
import ai.examin.core.enums.CourseStatus;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Role;
import ai.examin.core.enums.SpecialistProgramStatus;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.utils.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static ai.examin.admin.model.course.mapper.CourseMapper.toResponse;
import static ai.examin.admin.model.course.mapper.CourseMentorMapper.toEntity;
import static ai.examin.core.enums.CourseStatus.DELETED;
import static ai.examin.core.enums.SpecialistProgramStatus.ASSIGNED;
import static ai.examin.core.enums.SpecialistProgramStatus.UNASSIGNED;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseMentorService {
    private final CourseRepository courseRepository;
    private final AuthServiceClient authServiceClient;
    private final CourseMentorRepository courseMentorRepository;
    private final ProgramExpertRepository programExpertRepository;
    private final UserContextService userContextService;


    public CourseResponse assignMentor(Long courseId, UUID mentorId) {
        Course course = courseRepository.findByIdAndStatusNot(courseId, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        UserResponse mentor = authServiceClient.getUserById(mentorId);
        if (!mentor.getRole().equals(Role.MENTOR))
            throw new ApiException(ResponseStatus.ROLE_MISMATCH_EXCEPTION);

        if (userContextService.getCurrentUserRoles().contains(Role.SUPERVISOR.name())) {
            Optional<CourseMentor> optionalCourseMentor = courseMentorRepository.findByCourse_IdAndMentorId(courseId, mentorId);
            if (optionalCourseMentor.isEmpty()) {
                courseMentorRepository.saveAndFlush(toEntity(course, mentor));
            } else {
                CourseMentor courseMentor = optionalCourseMentor.get();
                if (courseMentor.getStatus().equals(ASSIGNED))
                    throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_ASSIGNED);

                courseMentor.setStatus(ASSIGNED);
                courseMentorRepository.saveAndFlush(courseMentor);
            }
        }
        else {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), getExpertId(), ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            Optional<CourseMentor> optionalCourseMentor = courseMentorRepository.findByCourse_IdAndMentorId(courseId, mentorId);
            if (optionalCourseMentor.isEmpty()) {
                log.info("Course Mentor not found, creating new one");
                // TODO: send approval request to supervisor to assign the mentor
            } else {
                CourseMentor courseMentor = optionalCourseMentor.get();
                if (courseMentor.getStatus().equals(ASSIGNED))
                    throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_ASSIGNED);

                // TODO: send approval request to supervisor to assign the mentor
            }

        }

        return toResponse(course, courseMentorRepository.findAllByCourseId(course.getId()));
    }

    public CourseResponse unassignMentor(Long courseId, UUID mentorId) {
        Course course = courseRepository.findByIdAndStatusNot(courseId, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        if (userContextService.getCurrentUserRoles().contains(Role.SUPERVISOR.name())) {
            CourseMentor courseMentor = courseMentorRepository
                .findByCourse_IdAndMentorId(course.getId(), mentorId)
                .orElseThrow(() -> new ApiException(ResponseStatus.SPECIALIST_NOT_FOUND));

            if (courseMentor.getStatus().equals(UNASSIGNED))
                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_UNASSIGNED);

            courseMentor.setStatus(UNASSIGNED);
            courseMentorRepository.saveAndFlush(courseMentor);
        } else {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), getExpertId(), ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            CourseMentor courseMentor = courseMentorRepository
                .findByCourse_IdAndMentorId(course.getId(), mentorId)
                .orElseThrow(() -> new ApiException(ResponseStatus.SPECIALIST_NOT_FOUND));

            if (courseMentor.getStatus().equals(UNASSIGNED))
                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_UNASSIGNED);

            // TODO: send approval request to supervisor to unassign the mentor
        }

        return toResponse(course, courseMentorRepository.findAllByCourseId(course.getId()));
    }

    private UUID getExpertId() {
        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context in CourseService.getCurrentUserId()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        return UUID.fromString(currentUserId.get());
    }
}
