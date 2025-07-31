package ai.examin.auth.model.user;

import enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndStatus(String email, Status status);
    Boolean existsByEmailAndStatusNot(String email, Status status);
}
