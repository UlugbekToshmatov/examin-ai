package ai.examin.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${spring.rabbitmq.auth.queue}")
    private String authQueue;
    @Value("${spring.rabbitmq.auth.exchange}")
    private String authExchange;
    @Value("${spring.rabbitmq.auth.route}")
    private String authRoute;
    @Value("${spring.rabbitmq.email.queue}")
    private String emailQueue;
    @Value("${spring.rabbitmq.email.exchange}")
    private String emailExchange;
    @Value("${spring.rabbitmq.email.route}")
    private String emailRoute;


    // *** Auth Service Notification Properties
    @Bean(name = "authQueue")
    public Queue authQueue() {
        return new Queue(authQueue);
    }

    @Bean(name = "authExchange")
    public TopicExchange authExchange() {
        return new TopicExchange(authExchange);
    }

    @Bean(name = "authBinding")
    public Binding authBinding() {
        return BindingBuilder
            .bind(authQueue())
            .to(authExchange())
            .with(authRoute);
    }
    // Auth Service Notification Properties ***

    // *** Email Notification Properties
    @Bean(name = "emailQueue")
    public Queue emailQueue() {
        return new Queue(emailQueue);
    }

    @Bean(name = "emailExchange")
    public TopicExchange emailExchange() {
        return new TopicExchange(emailExchange);
    }

    @Bean(name = "emailBinding")
    public Binding emailBinding() {
        return BindingBuilder
            .bind(emailQueue())
            .to(emailExchange())
            .with(emailRoute);
    }
    // Email Notification Properties ***
}
