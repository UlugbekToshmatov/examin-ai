package ai.examin.admin.model.task.repository;

import ai.examin.admin.model.task.entity.TaskIntern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskInternRepository extends JpaRepository<TaskIntern, Long> {
    @Query(value = """
        SELECT ti FROM TaskIntern ti
        WHERE ti.task.id = :taskId AND ti.status = 'ASSIGNED'
    """)
    List<TaskIntern> findAllByTaskId(@PathVariable("taskId") Long taskId);

    Optional<TaskIntern> findByTask_IdAndInternId(Long task_id, UUID internId);
}
