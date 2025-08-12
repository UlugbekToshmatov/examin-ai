package ai.examin.notification.model.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;


    public void sendEmail(Map<String, Object> payload) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(payload.get("email").toString());
        mailMessage.setSubject("Email Confirmation");
        mailMessage.setText("Hello " + payload.get("firstName").toString() + payload.get("lastName").toString() + ","
            + "\n\nPlease, confirm your email address by clicking the link below:\n" + payload.get("link").toString()
            + "\n\nNote that the link expires in 10 minutes.\n\nBest regards,\nExaminAI Team");
        mailSender.send(mailMessage);
        log.info("Sent email details: {}", mailMessage);
    }
}
