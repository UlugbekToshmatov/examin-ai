package ai.examin.admin.model.program.entity;

import ai.examin.core.base_classes.BaseEntity;
import ai.examin.core.enums.ProgramStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "programs")
@Entity
public class Program extends BaseEntity {
    @Id
    @SequenceGenerator(name = "programs_id_seq", sequenceName = "programs_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "programs_id_seq")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "supervisor_id", nullable = false)
    private UUID supervisorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'CREATED'")
    private ProgramStatus status = ProgramStatus.CREATED;


    @Override
    public void softDelete(UUID currentUserId) {
        if (status != null && !status.equals(ProgramStatus.DELETED)) {
            if (getDeletedAt() == null) setDeletedAt(LocalDateTime.now());
            if (getDeletedBy() == null) setDeletedBy(currentUserId);
            status = ProgramStatus.DELETED;
            name = getDeletedName(getName());
        }
    }

    private String getDeletedName(String name) {
        return String.format(
            "%s_deleted_at_%s",
            name.replace(" ", "-"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss:SSS").format(getDeletedAt())
        );
    }
}
