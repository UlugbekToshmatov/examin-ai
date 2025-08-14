package ai.examin.admin.model.program;

import ai.examin.admin.model.course.Course;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
@Table(name = "programs")
@Entity
public class Program {
    @Id
    @SequenceGenerator(name = "programs_id_seq", sequenceName = "programs_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "programs_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "expert_id", nullable = false)
    private Long expertId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "approved", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean approved = false;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", updatable = false)
    private LocalDateTime deletedAt;
}
