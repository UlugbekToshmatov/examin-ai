package ai.examin.admin.model.task.entity;

import ai.examin.admin.model.course.entity.Course;
import ai.examin.core.base_classes.BaseEntity;
import ai.examin.core.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name = "tasks", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "title"}))
@Entity
public class Task extends BaseEntity {
    @Id
    @SequenceGenerator(name = "tasks_id_seq", sequenceName = "tasks_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasks_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false, updatable = false)
    private Course course;

    @Column(name = "mentor_id", nullable = false)
    private UUID mentorId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "definition", nullable = false, length = 500)
    private String definition;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'IN_PROGRESS'")
    private TaskStatus status = TaskStatus.IN_PROGRESS;


    @Override
    public void softDelete(UUID currentUserId) {
        if (status != null && !status.equals(TaskStatus.DELETED)) {
            if (getDeletedAt() == null) setDeletedAt(LocalDateTime.now());
            if (getDeletedBy() == null) setDeletedBy(currentUserId);
            status = TaskStatus.DELETED;
            title = getDeletedTitle(getTitle());
        }
    }

    private String getDeletedTitle(String title) {
        return String.format(
            "%s_deleted_at_%s",
            title.replace(" ", "-"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss:SSS").format(getDeletedAt())
        );
    }
}
