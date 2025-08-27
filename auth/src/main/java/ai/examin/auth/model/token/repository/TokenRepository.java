package ai.examin.auth.model.token.repository;

import ai.examin.auth.model.token.entity.Token;
import ai.examin.auth.model.user.entity.User;
import ai.examin.core.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
//    Optional<Token> findByUser_IdAndTypeAndRevokedFalseAndExpiredFalse(Long user_id, TokenType type);
    Optional<Token> findByUser_IdAndTypeAndRevokedFalseAndExpiredFalse(UUID user_id, TokenType type);
    List<Token> findAllByUserAndTypeInAndRevokedFalseAndExpiredFalse(User user, Collection<TokenType> type);

    Optional<Token> findByJwtTokenAndType(String jwtToken, TokenType type);
    Optional<Token> findByJwtTokenAndTypeAndRevokedFalseAndExpiredFalse(String jwtToken, TokenType type);
}
