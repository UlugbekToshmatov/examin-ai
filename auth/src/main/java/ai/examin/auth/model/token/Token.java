package ai.examin.auth.model.token;

import ai.examin.auth.model.user.User;
import ai.examin.core.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static java.lang.Boolean.FALSE;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
@Table(name = "tokens")
@Entity
public class Token {
    @Id
    @SequenceGenerator(name = "tokens_id_seq", sequenceName = "tokens_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tokens_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "jwt_token", nullable = false, unique = true)
    private String jwtToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TokenType type;

    @Column(name = "issued_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean revoked = FALSE;

    @Column(name = "expired", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean expired = FALSE;
}
