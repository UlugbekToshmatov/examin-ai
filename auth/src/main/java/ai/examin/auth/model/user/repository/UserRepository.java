package ai.examin.auth.model.user.repository;

import ai.examin.auth.model.user.entity.User;
import ai.examin.core.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByIdAndStatus(UUID id, Status status);
    Optional<User> findByEmailAndStatus(String email, Status status);
    Boolean existsByEmailAndStatusNot(String email, Status status);
    Boolean existsByUsernameAndStatusNot(String username, Status status);
}
