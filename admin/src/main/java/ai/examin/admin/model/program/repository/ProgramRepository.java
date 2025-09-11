package ai.examin.admin.model.program.repository;

import ai.examin.admin.model.program.entity.Program;
import ai.examin.core.enums.ProgramStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    Boolean existsByNameAndStatusNot(String name, ProgramStatus status);
    Optional<Program> findByIdAndStatusNot(Long id, ProgramStatus status);
    List<Program> findAllByStatusNot(ProgramStatus courseStatus);
}
