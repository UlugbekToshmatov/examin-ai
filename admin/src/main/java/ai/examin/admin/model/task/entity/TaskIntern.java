package ai.examin.admin.model.task.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name = "task_interns", uniqueConstraints = {@UniqueConstraint(columnNames = {"task_id", "intern_id"})})
@Entity
public class TaskIntern {
    @Id
    @SequenceGenerator(name = "task_interns_id_seq", sequenceName = "task_interns_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_interns_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "intern_id", nullable = false)
    private Long internId;

    @Column(name = "github_link", nullable = false, columnDefinition = "VARCHAR(500) DEFAULT 'NOT SUBMITTED'")
    private String githubLink;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
