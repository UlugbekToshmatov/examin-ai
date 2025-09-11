package ai.examin.core.security;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.UUID;

@Data
@Builder
public class UserContext {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Collection<String> roles;
}
