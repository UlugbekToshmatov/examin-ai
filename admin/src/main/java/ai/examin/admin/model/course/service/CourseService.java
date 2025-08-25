package ai.examin.admin.model.course.service;

import ai.examin.admin.model.course.dto.CourseRequest;
import ai.examin.admin.model.course.dto.CourseResponse;
import ai.examin.admin.model.course.dto.CourseResponseMin;
import ai.examin.admin.model.course.dto.CourseUpdateRequest;
import ai.examin.admin.model.course.entity.Course;
import ai.examin.admin.model.course.repository.CourseRepository;
import ai.examin.core.enums.CourseStatus;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.exception_handler.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ai.examin.admin.model.course.mapper.CourseMapper.*;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;


    public List<CourseResponseMin> getAll() {
        return toResponseList(courseRepository.findAllByStatusNot(CourseStatus.DELETED));
    }

    public CourseResponse getById(Long id) {
        return toResponse(courseRepository.findByIdAndStatusNot(id, CourseStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND)));
    }

    public CourseResponse create(CourseRequest request) {
        if (courseRepository.existsByNameAndStatusNot(request.name(), CourseStatus.DELETED))
            throw new ApiException(ResponseStatus.COURSE_ALREADY_EXISTS);

        Course course = toEntity(request);
        return toResponse(courseRepository.save(course));
    }

    public CourseResponse updateById(Long id, CourseUpdateRequest request) {
        Course course = courseRepository.findByIdAndStatusNot(id, CourseStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        if (courseRepository.existsByNameAndStatusNot(request.name(), CourseStatus.DELETED))
            throw new ApiException(ResponseStatus.COURSE_ALREADY_EXISTS);

        update(request, course);

        return toResponse(courseRepository.save(course));
    }

    public void delete(Long id) {
        Course course = courseRepository.findByIdAndStatusNot(id, CourseStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        course.setStatus(CourseStatus.DELETED);
        course.setName(getDeletedName(course.getName()));
        courseRepository.save(course);
    }

    private String getDeletedName(String name) {
        return String.format("%s_deleted_at_%s", name, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss:SSS")));
    }
}
