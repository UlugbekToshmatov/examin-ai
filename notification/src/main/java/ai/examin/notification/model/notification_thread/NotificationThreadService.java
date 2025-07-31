package ai.examin.notification.model.notification_thread;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationThreadService {
    private final NotificationThreadRepository notificationThreadRepository;
}
