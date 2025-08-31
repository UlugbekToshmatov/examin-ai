package ai.examin.auth.model.user.entity;

import ai.examin.core.base_classes.BaseEntity;
import ai.examin.core.enums.Role;
import ai.examin.core.enums.Status;
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
@Table(name = "users")
@Entity
public class User extends BaseEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "image_url", columnDefinition = "VARCHAR(255) DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png'")
    private String imageUrl = "https://cdn-icons-png.flaticon.com/512/149/149071.png";


    @Override
    public void softDelete(UUID currentUserId) {
        if (status != null && !status.equals(Status.DELETED)) {
            if (getDeletedAt() == null) setDeletedAt(LocalDateTime.now());
            if (getDeletedBy() == null) setDeletedBy(currentUserId);
            status = Status.DELETED;
            username = String.format("%s_deleted_at_%s", username, DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss:SSS").format(getDeletedAt()));
            email = String.format("%s_deleted_at_%s", email, DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss:SSS").format(getDeletedAt()));
        }
    }
}
