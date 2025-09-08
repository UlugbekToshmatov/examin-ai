package ai.examin.admin.model.course.entity;

import ai.examin.core.base_classes.BaseEntity;
import ai.examin.core.enums.SpecialistProgramStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name = "courses_mentors", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "mentor_id"}))
@Entity
public class CourseMentor extends BaseEntity {
    @Id
    @SequenceGenerator(name = "courses_mentors_id_seq", sequenceName = "courses_mentors_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "courses_mentors_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "mentor_id", nullable = false)
    private UUID mentorId;

    @Column(name = "mentor_full_name", nullable = false)
    private String mentorFullName;

    @Column(name = "mentor_email", nullable = false)
    private String mentorEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'ASSIGNED'")
    private SpecialistProgramStatus status = SpecialistProgramStatus.ASSIGNED;


    @Override
    public void softDelete(UUID currentUserId) {

    }
}
