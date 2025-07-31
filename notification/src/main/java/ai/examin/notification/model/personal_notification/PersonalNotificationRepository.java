package ai.examin.notification.model.personal_notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalNotificationRepository extends JpaRepository<PersonalNotification, Long> {
}
