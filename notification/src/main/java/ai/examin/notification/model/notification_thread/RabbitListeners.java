package ai.examin.notification.model.notification_thread;

import ai.examin.notification.model.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitListeners {
    private final EmailService emailService;

    @RabbitListener(
        id = "${spring.rabbitmq.auth.id}",
        queues = "${spring.rabbitmq.auth.queue}",
        containerFactory = "rabbitListenerContainerFactory"
    )
    public void consumeAuthMessage(Map<String, Object> payload, Message message) {
        try {
            log.info("Received auth notification message: {}\nPayload: {}",
                   message,
                   payload);
            // Process the message here
            emailService.sendEmail(payload);
        } catch (Exception e) {
            log.error("Error processing auth message: {}", e.getMessage(), e);
            // Consider implementing dead letter queue handling here
            throw e;
        }
    }


    @RabbitListener(
        id = "${spring.rabbitmq.email.id}",
        queues = "${spring.rabbitmq.email.queue}",
        containerFactory = "rabbitListenerContainerFactory"
    )
    public void consumeEmailMessage(Map<String, Object> payload, Message message) {
        try {
            log.info("Received email message after {} ms: {}",
                   message.getMessageProperties().getReceivedDelayLong(),
                   payload);
            // Process the email here
        } catch (Exception e) {
            log.error("Error processing email message: {}", e.getMessage(), e);
            // Consider implementing dead letter queue handling here
            throw e;
        }
    }

}
