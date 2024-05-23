package be.vlaanderen.informatievlaanderen.ldes.server;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootConfiguration
public class MongoTestConfiguration {

	@Bean
	public MongoDBContainer mongoDBContainer() {
		var container = new MongoDBContainer("mongo:latest")
				.withExposedPorts(27017);
		container.start();
		return container;
	}

	@Bean
	@DependsOn("mongoDBContainer")
	@ConditionalOnProperty(name = "ldes-server.migrate-mongo", havingValue = "true")
	public MongoDatabaseFactory mongoDatabaseFactory(MongoDBContainer mongo) {
		return new SimpleMongoClientDatabaseFactory(mongo.getConnectionString() + "/test");
	}

	@Bean
	@DependsOn("mongoDatabaseFactory")
	@ConditionalOnProperty(name = "ldes-server.migrate-mongo", havingValue = "true")
	public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory) {
		return new MongoTemplate(mongoDbFactory);
	}
}
