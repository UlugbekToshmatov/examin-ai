package ai.examin.auth.model.user.dto;

import ai.examin.auth.model.user.entity.User;
import ai.examin.auth.model.user.mapper.UserMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginResponse {
    private UserResponse user;
    private String accessToken;
    private String refreshToken;


    public LoginResponse(User user, String accessToken, String refreshToken) {
        this.user = UserMapper.toResponse(user);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
