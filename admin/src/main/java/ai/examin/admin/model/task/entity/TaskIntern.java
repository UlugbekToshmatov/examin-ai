package ai.examin.admin.model.task.entity;

import ai.examin.core.base_classes.BaseEntity;
import ai.examin.core.enums.SpecialistProgramStatus;
import ai.examin.core.enums.TaskInternStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name = "tasks_interns", uniqueConstraints = {@UniqueConstraint(columnNames = {"task_id", "intern_id"})})
@Entity
public class TaskIntern extends BaseEntity {
    @Id
    @SequenceGenerator(name = "task_interns_id_seq", sequenceName = "task_interns_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_interns_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "intern_id", nullable = false)
    private UUID internId;

    @Column(name = "intern_full_name", nullable = false)
    private String internFullName;

    @Column(name = "intern_email", nullable = false)
    private String internEmail;

    @Column(name = "github_link", nullable = false, columnDefinition = "VARCHAR(500) DEFAULT 'NOT SUBMITTED'")
    private String githubLink;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'IN_PROGRESS'")
    private TaskInternStatus taskStatus = TaskInternStatus.IN_PROGRESS;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'ASSIGNED'")
    private SpecialistProgramStatus status = SpecialistProgramStatus.ASSIGNED;


    @Override
    public void softDelete(UUID currentUserId) {

    }
}
