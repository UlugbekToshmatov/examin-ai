package ai.examin.notification.model.error_notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorNotificationRepository extends JpaRepository<ErrorNotification, Long> {
}
