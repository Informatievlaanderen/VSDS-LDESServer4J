package be.vlaanderen.informatievlaanderen.ldes.server;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = { @Server(url = "/", description = "Default Server URL") })
@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		// Auto create Spring Batch Tables
		System.setProperty("spring.batch.jdbc.initialize-schema", "always");
		System.setProperty("spring.batch.jdbc.isolation-level-for-create", "READ_COMMITTED");
		SpringApplication.run(Application.class, args);
	}


}
