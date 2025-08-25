package ai.examin.admin.model.course.repository;

import ai.examin.admin.model.course.entity.Course;
import ai.examin.core.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Boolean existsByNameAndStatusNot(String name, CourseStatus status);
    Optional<Course> findByIdAndStatusNot(Long id, CourseStatus status);
    List<Course> findAllByStatusNot(CourseStatus courseStatus);
}
