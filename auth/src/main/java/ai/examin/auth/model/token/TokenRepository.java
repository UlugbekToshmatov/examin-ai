package ai.examin.auth.model.token;

import ai.examin.core.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUser_IdAndTypeAndRevokedFalseAndExpiredFalse(Long user_id, TokenType type);
}
