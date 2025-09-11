package ai.examin.auth.model.user.dto;

import ai.examin.core.enums.Role;
import ai.examin.core.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String imageUrl;
    private Role role;
    private Status status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;
}
