package ai.examin.auth.model.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final RabbitTemplate rabbitTemplate;
    @Value("${spring.rabbitmq.auth.exchange}")
    private String exchange;
    @Value("${spring.rabbitmq.auth.route}")
    private String route;


    public void sendNotification(Map<String, Object> notification) {
        rabbitTemplate.convertAndSend(
            exchange, route, notification
        );
    }
}
