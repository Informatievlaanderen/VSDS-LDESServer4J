package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.LdesFragmentMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.repository.LdesFragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.MemberMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.repository.LdesMemberEntityRepository;
import io.micrometer.observation.ObservationRegistry;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.observability.ContextProviderFactory;
import org.springframework.data.mongodb.observability.MongoObservationCommandListener;

@Configuration
@ConditionalOnClass(MemberMongoRepository.class)
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
@EnableMongock
public class MongoAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public MemberRepository ldesMemberMongoRepository(final LdesMemberEntityRepository ldesMemberEntityRepository,
			final MongoTemplate mongoTemplate) {
		return new MemberMongoRepository(ldesMemberEntityRepository, mongoTemplate);
	}

	@Bean
	@ConditionalOnMissingBean
	public LdesFragmentRepository ldesFragmentMongoRepository(
			final LdesFragmentEntityRepository ldesFragmentEntityRepository, final MongoTemplate mongoTemplate) {
		return new LdesFragmentMongoRepository(ldesFragmentEntityRepository, mongoTemplate);
	}

	@Bean
	MongoClientSettingsBuilderCustomizer mongoMetricsSynchronousContextProvider(ObservationRegistry registry) {
		return clientSettingsBuilder -> clientSettingsBuilder.contextProvider(ContextProviderFactory.create(registry))
				.addCommandListener(new MongoObservationCommandListener(registry));
	}

	@Bean
	public ConnectionDriver mongockConnection(MongoTemplate mongoTemplate) {
		return SpringDataMongoV4Driver.withDefaultLock(mongoTemplate);
	}
}
