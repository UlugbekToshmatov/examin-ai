package ai.examin.auth.model.user;

import ai.examin.core.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndStatus(String email, Status status);
    Boolean existsByEmailAndStatusNot(String email, Status status);

//    @Query("""
//        SELECT new ai.examin.core.security.UserPayload(
//            u.id, u.firstName, u.lastName, u.email, u.password, u.role, u.status, t.token
//        ) FROM User u JOIN Token t ON u.id = t.user.id
//        WHERE u.email = :email AND u.status = 'ACTIVE'
//    """)
//    UserPayload findUserWithRoleByEmail(String email);
}
