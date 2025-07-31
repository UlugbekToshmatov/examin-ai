package ai.examin.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import utils.UtilityTools;

@SpringBootApplication(scanBasePackages = {"ai.examin.auth"})
@Import(UtilityTools.class)
public class AuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

}
