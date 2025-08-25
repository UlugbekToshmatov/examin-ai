package ai.examin.admin.model.task.entity;

import ai.examin.admin.model.program.entity.Program;
import ai.examin.core.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name = "tasks", uniqueConstraints = @UniqueConstraint(columnNames = {"program_id", "title"}))
@Entity
public class Task {
    @Id
    @SequenceGenerator(name = "tasks_id_seq", sequenceName = "tasks_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasks_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false, updatable = false)
    private Program program;

    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    @Column(name = "definition", nullable = false)
    private String definition;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", updatable = false)
    private LocalDateTime deletedAt;
}
