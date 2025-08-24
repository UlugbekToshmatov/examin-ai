package ai.examin.core.security;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class UserContext {
    private Long userId;
    private String externalId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Collection<String> roles;
}
