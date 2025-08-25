package ai.examin.admin.model.program.repository;

import ai.examin.admin.model.program.entity.Program;
import ai.examin.core.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    Optional<Program> findByIdAndStatusNot(Long id, Status status);
    List<Program> findAllByStatusNot(Status status);
}
