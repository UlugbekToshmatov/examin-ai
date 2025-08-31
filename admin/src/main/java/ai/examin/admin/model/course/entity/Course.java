package ai.examin.admin.model.course.entity;

import ai.examin.admin.model.program.entity.Program;
import ai.examin.core.base_classes.BaseEntity;
import ai.examin.core.enums.CourseStatus;
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
@Table(name = "courses", uniqueConstraints = @UniqueConstraint(columnNames = {"program_id", "name"}))
@Entity
public class Course extends BaseEntity {
    @Id
    @SequenceGenerator(name = "courses_id_seq", sequenceName = "courses_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "courses_id_seq")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Column(name = "expert_id", nullable = false)
    private UUID expertId;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'CREATED'")
    private CourseStatus status = CourseStatus.CREATED;


    @Override
    public void softDelete(UUID currentUserId) {
        if (status != null && !status.equals(CourseStatus.DELETED)) {
            if (getDeletedAt() == null) setDeletedAt(LocalDateTime.now());
            if (getDeletedBy() == null) setDeletedBy(currentUserId);
            status = CourseStatus.DELETED;
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
