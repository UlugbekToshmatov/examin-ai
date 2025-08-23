package ai.examin.auth.model.token.mapper;

import ai.examin.auth.model.token.entity.Token;
import ai.examin.auth.model.user.entity.User;
import ai.examin.core.enums.TokenType;

import java.time.Duration;
import java.time.LocalDateTime;

import static ai.examin.core.security.JwtProviderParams.*;
import static ai.examin.core.security.JwtProviderParams.ACCOUNT_VERIFICATION_TOKEN_EXPIRATION;

public class TokenMapper {

    public static Token toToken(User user, String jwtToken, TokenType tokenType) {
        Long tokenExpiration = null;
        switch (tokenType) {
            case ACCESS_TOKEN -> tokenExpiration = ACCESS_TOKEN_EXPIRATION;
            case REFRESH_TOKEN -> tokenExpiration = REFRESH_TOKEN_EXPIRATION;
            case ACCOUNT_VERIFICATION_TOKEN -> tokenExpiration = ACCOUNT_VERIFICATION_TOKEN_EXPIRATION;
            case PASSWORD_RESET_TOKEN -> tokenExpiration = RESET_PASSWORD_TOKEN_EXPIRATION;
        }

        return Token.builder()
            .user(user)
            .jwtToken(jwtToken)
            .type(tokenType)
            .issuedAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(tokenExpiration)))
            .revoked(false)
            .expired(false)
            .build();
    }

}
