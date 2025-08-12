package ai.examin.notification.model.personal_notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalNotificationService {
    private final PersonalNotificationRepository personalNotificationRepository;
}
