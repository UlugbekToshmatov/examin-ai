package ai.examin.notification.model.error_notification;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
@Table(name = "error_notification")
@Entity
public class ErrorNotification {
    @Id
    @SequenceGenerator(name = "error_notification_id_seq", sequenceName = "error_notification_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "error_notification_id_seq")
    private Long id;

    @Column(name = "service_name", nullable = false, updatable = false)
    private String serviceName;

    @Column(name = "exception_name", nullable = false, updatable = false)
    private String exceptionName;

    @Column(name = "method_name", updatable = false)
    private String methodName;

    @Column(name = "endpoint", updatable = false)
    private String endpoint;

    @Column(name = "status_code", nullable = false, updatable = false)
    private Integer statusCode;

    @Column(name = "message", updatable = false, length = 512)
    private String message;

    @Column(name = "stack_trace", nullable = false, updatable = false)
    private String stackTrace;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
}
