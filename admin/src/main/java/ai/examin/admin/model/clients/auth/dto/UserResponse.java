package ai.examin.admin.model.clients.auth.dto;

import ai.examin.core.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class UserResponse {
    private Long id;
    private String externalId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String imageUrl;
    private Role role;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
