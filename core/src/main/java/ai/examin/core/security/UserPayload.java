package ai.examin.core.security;

import ai.examin.core.enums.Status;
import ai.examin.core.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPayload {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;
    private Status status;
    private String accessToken;
    private String refreshToken;
}
