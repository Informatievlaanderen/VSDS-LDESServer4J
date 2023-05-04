package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.repository.LdesConfigRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository.SnapshotRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstream.EventStreamMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.LdesFragmentMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.repository.LdesFragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.ldesconfig.LdesConfigMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.ldesconfig.repository.LdesConfigEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.MemberMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.repository.LdesMemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape.ShaclShapeMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape.repository.ShaclShapeEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.SnapshotMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.repository.SnapshotEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.ViewMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.repository.ViewEntityRepository;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.observability.ContextProviderFactory;
import org.springframework.data.mongodb.observability.MongoObservationCommandListener;

@Configuration
@ConditionalOnClass(MemberMongoRepository.class)
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
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
	@ConditionalOnMissingBean
	public LdesConfigRepository ldesConfigMongoRepository(
			final LdesConfigEntityRepository ldesConfigEntityRepository) {
		return new LdesConfigMongoRepository(ldesConfigEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public SnapshotRepository snapshotMongoRepository(
			final SnapshotEntityRepository snapshotEntityRepository) {
		return new SnapshotMongoRepository(snapshotEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public EventStreamRepository eventStreamRepository(final EventStreamEntityRepository eventStreamEntityRepository) {
		return new EventStreamMongoRepository(eventStreamEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public ShaclShapeRepository shaclShapeMongoRepository(final ShaclShapeEntityRepository shaclShapeEntityRepository) {
		return new ShaclShapeMongoRepository(shaclShapeEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public ViewRepository viewMongoRepository(final ViewEntityRepository viewEntityRepository) {
		return new ViewMongoRepository(viewEntityRepository);
	}

	@Profile("monitoring") // This config can cause memory overflow issues when running large database
	// migrations.
	@Bean
	MongoClientSettingsBuilderCustomizer mongoMetricsSynchronousContextProvider(ObservationRegistry registry) {
		return clientSettingsBuilder -> clientSettingsBuilder.contextProvider(ContextProviderFactory.create(registry))
				.addCommandListener(new MongoObservationCommandListener(registry));
	}

}
