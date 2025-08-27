package ai.examin.core.security;

import ai.examin.core.enums.Status;
import ai.examin.core.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPayload {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private Status status;
    private String accessToken;
    private String refreshToken;
}
