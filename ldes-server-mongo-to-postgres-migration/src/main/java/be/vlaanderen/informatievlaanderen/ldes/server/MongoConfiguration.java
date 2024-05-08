package be.vlaanderen.informatievlaanderen.ldes.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoConfiguration {

	@Value("${spring.data.mongodb.uri}")
	private String mongoUri;

	@Bean
	@ConditionalOnProperty(name = "ldes-server.migrate-mongo", havingValue = "true")
	public MongoDatabaseFactory mongoDatabaseFactory() {
		return new SimpleMongoClientDatabaseFactory(mongoUri);
	}

	@Bean
	@ConditionalOnProperty(name = "ldes-server.migrate-mongo", havingValue = "true")
	public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory) {
		return new MongoTemplate(mongoDbFactory);
	}
}
