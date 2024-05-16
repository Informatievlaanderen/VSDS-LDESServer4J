package be.vlaanderen.informatievlaanderen.ldes.server;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@OpenAPIDefinition(servers = { @Server(url = "/", description = "Default Server URL") })
//TODO Cleanup after 3.0 release
@SpringBootApplication(exclude = {
		MongoAutoConfiguration.class,
		MongoRepositoriesAutoConfiguration.class,
		MongoDataAutoConfiguration.class
})
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
