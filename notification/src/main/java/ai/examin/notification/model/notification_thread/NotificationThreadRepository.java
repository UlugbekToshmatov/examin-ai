package ai.examin.notification.model.notification_thread;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationThreadRepository extends JpaRepository<NotificationThread, Long> {
}
