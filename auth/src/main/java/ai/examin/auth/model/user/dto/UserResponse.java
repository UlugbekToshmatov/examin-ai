package ai.examin.auth.model.user.dto;

import ai.examin.core.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String externalId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String imageUrl;
    private UserRole role;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
