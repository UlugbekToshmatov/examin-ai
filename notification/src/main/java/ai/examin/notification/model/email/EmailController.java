package ai.examin.notification.model.email;

import ai.examin.core.base_classes.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;


    @PostMapping("/send/{email}")
    public ResponseEntity<HttpResponse> sendEmail(@PathVariable String email) {
//        CompletableFuture.runAsync(() -> emailService.sendEmail(email));
        return ResponseEntity.ok(
            HttpResponse.builder().statusCode(200).description("Email sent successfully").build()
        );
    }
}
