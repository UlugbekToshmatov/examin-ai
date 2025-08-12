package ai.examin.core.security;

import ai.examin.core.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private UserPayload userPayload;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.userPayload.getRole().name()));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.userPayload.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return this.userPayload.getEmail();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !this.userPayload.getStatus().equals(Status.BLOCKED);
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.userPayload.getStatus().equals(Status.ACTIVE);
    }
}
