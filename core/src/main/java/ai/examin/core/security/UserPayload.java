package ai.examin.core.security;

import ai.examin.core.enums.Status;
import ai.examin.core.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class UserPayload {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;
    private Status status;
    private String accessToken;
}
