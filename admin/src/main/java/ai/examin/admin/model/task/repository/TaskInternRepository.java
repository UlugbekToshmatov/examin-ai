package ai.examin.admin.model.task.repository;

import ai.examin.admin.model.task.entity.TaskIntern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskInternRepository extends JpaRepository<TaskIntern, Long> {
}
