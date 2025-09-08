package ai.examin.admin.model.program.entity;

import ai.examin.core.base_classes.BaseEntity;
import ai.examin.core.enums.SpecialistProgramStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "programs_experts", uniqueConstraints = {@UniqueConstraint(columnNames = {"program_id", "expert_id"})})
@Entity
public class ProgramExpert extends BaseEntity {
    @Id
    @SequenceGenerator(name = "programs_experts_id_seq", sequenceName = "programs_experts_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "programs_experts_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false, updatable = false)
    private Program program;

    @Column(name = "expert_id", nullable = false, updatable = false)
    private UUID expertId;

    @Column(name = "expert_full_name", nullable = false)
    private String expertFullName;

    @Column(name = "expert_email", nullable = false)
    private String expertEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'ASSIGNED'")
    private SpecialistProgramStatus status = SpecialistProgramStatus.ASSIGNED;


    @Override
    public void softDelete(UUID currentUserId) {

    }
}
