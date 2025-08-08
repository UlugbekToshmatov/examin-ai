package ai.examin.auth.model.account_verification;

import ai.examin.auth.model.user.User;
import ai.examin.core.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
@Table(name = "account_verifications", uniqueConstraints = {
    @UniqueConstraint(name = "unq_account_verifications_user_id_url", columnNames = {"user_id", "url"})
})
@Entity
public class AccountVerification {
    @Id
    @SequenceGenerator(name = "account_verifications_id_seq", sequenceName = "account_verifications_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_verifications_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "url", nullable = false, updatable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "deleted_at", updatable = false)
    private LocalDateTime deletedAt;
}
