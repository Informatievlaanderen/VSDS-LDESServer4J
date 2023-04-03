package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongock;

import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
@EnableMongock
public class Config {

	@Bean
	public ConnectionDriver mongockConnection(MongoTemplate mongoTemplate) {
		return SpringDataMongoV4Driver.withDefaultLock(mongoTemplate);
	}
}
