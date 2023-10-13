package root;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "RM API", version = "v1"))
public class RelexMessengerApplication {
	public static void main(String[] args) {
		SpringApplication.run(RelexMessengerApplication.class, args);
	}
}
