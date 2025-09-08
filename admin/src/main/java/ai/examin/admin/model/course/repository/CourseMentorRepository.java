package ai.examin.admin.model.course.repository;

import ai.examin.admin.model.course.entity.CourseMentor;
import ai.examin.core.enums.SpecialistProgramStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseMentorRepository extends JpaRepository<CourseMentor, Long> {
    @Query(value = """
        SELECT cm FROM CourseMentor cm 
        WHERE cm.course.id = :courseId AND cm.status = 'ASSIGNED'
    """)
    List<CourseMentor> findAllByCourseId(@PathVariable("courseId") Long courseId);
    Optional<CourseMentor> findByCourse_IdAndMentorIdAndStatus(Long course_id, UUID mentorId, SpecialistProgramStatus status);
}
