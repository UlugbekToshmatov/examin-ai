package ai.examin.notification.model.error_notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorNotificationService {
    private final ErrorNotificationRepository errorNotificationRepository;
}
