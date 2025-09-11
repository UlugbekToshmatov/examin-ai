package ai.examin.admin.model.program.repository;

import ai.examin.admin.model.program.entity.Program;
import ai.examin.admin.model.program.entity.ProgramExpert;
import ai.examin.core.enums.SpecialistProgramStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgramExpertRepository extends JpaRepository<ProgramExpert, Long> {
    @Query(value = """
        SELECT pe FROM ProgramExpert pe
        WHERE pe.program.id = :programId AND pe.status = 'ASSIGNED'
    """)
    List<ProgramExpert> findAllByProgramId(@PathVariable("programId") Long programId);

    Optional<ProgramExpert> findByProgram_IdAndExpertId(Long program_id, UUID expertId);
    Boolean existsByProgramAndExpertIdAndStatus(Program program, UUID expertId, SpecialistProgramStatus status);
}
