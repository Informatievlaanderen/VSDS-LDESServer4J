package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.MemberReferencesEntityRepository;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.observability.ContextProviderFactory;
import org.springframework.data.mongodb.observability.MongoObservationCommandListener;

@Configuration
@ConditionalOnClass(MemberMongoRepository.class)
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class MongoAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public MemberRepository ldesMemberMongoRepository(final LdesMemberEntityRepository ldesMemberEntityRepository) {
		return new MemberMongoRepository(ldesMemberEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public LdesFragmentRepository ldesFragmentMongoRepository(
			final LdesFragmentEntityRepository ldesFragmentEntityRepository) {
		return new LdesFragmentMongoRepository(ldesFragmentEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public MemberReferencesMongoRepository memberReferencesMongoRepository(
			final MemberReferencesEntityRepository memberReferencesEntityRepository) {
		return new MemberReferencesMongoRepository(memberReferencesEntityRepository);
	}

	@Bean
	MongoClientSettingsBuilderCustomizer mongoMetricsSynchronousContextProvider(ObservationRegistry registry) {
		return (clientSettingsBuilder) -> {
			clientSettingsBuilder.contextProvider(ContextProviderFactory.create(registry))
					.addCommandListener(new MongoObservationCommandListener(registry));
		};
	}
}
