package ai.examin.admin.model.task.entity;

import ai.examin.core.base_classes.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name = "task_interns", uniqueConstraints = {@UniqueConstraint(columnNames = {"task_id", "intern_id"})})
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

    @Column(name = "github_link", nullable = false, columnDefinition = "VARCHAR(500) DEFAULT 'NOT SUBMITTED'")
    private String githubLink;


    @Override
    public void softDelete(UUID currentUserId) {

    }
}
