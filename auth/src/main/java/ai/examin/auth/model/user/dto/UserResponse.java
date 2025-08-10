package ai.examin.auth.model.user.dto;

import ai.examin.core.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String imageUrl;
    private UserRole role;
    private LocalDateTime createdAt;
}
