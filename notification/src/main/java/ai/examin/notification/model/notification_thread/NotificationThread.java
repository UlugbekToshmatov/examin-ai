package ai.examin.notification.model.notification_thread;

import ai.examin.core.enums.ThreadActionType;
import ai.examin.core.enums.ThreadType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
@Table(name = "notification_thread")
@Entity
public class NotificationThread {
    @Id
    @SequenceGenerator(name = "notification_thread_id_seq", sequenceName = "notification_thread_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_thread_id_seq")
    private Long id;

    @Column(name = "admin_id", nullable = false, updatable = false)
    private Long adminId;

    @Enumerated(EnumType.STRING)
    @Column(name = "thread_action_type", nullable = false, updatable = false)
    private ThreadActionType threadActionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "thread_type", nullable = false, updatable = false)
    private ThreadType threadType;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
}
