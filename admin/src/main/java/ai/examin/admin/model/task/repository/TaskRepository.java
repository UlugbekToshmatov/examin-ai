package ai.examin.admin.model.task.repository;

import ai.examin.admin.model.task.entity.Task;
import ai.examin.core.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Boolean existsByCourse_IdAndTitleAndStatusNot(Long course_id, String title, TaskStatus status);
    Optional<Task> findByIdAndStatusNot(Long id, TaskStatus status);
    List<Task> findAllByStatusNot(TaskStatus status);
    List<Task> findAllByCourse_IdAndStatusNot(Long courseId, TaskStatus taskStatus);
}
