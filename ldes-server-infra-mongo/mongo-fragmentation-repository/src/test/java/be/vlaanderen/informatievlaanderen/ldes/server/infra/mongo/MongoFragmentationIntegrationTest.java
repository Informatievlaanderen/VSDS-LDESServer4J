package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.AllocationMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.FragmentMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.AllocationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.FragmentEntityRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataMongoTest
@ActiveProfiles("mongo-test")
@ContextConfiguration(classes = { FragmentEntityRepository.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.fragmentation",
		"be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo"})
@Import(MongoFragmentationIntegrationTest.EventStreamControllerTestConfiguration.class)
@SuppressWarnings("java:S2187")
public class MongoFragmentationIntegrationTest {

	@Autowired
	public FragmentRepository fragmentRepository;

	@TestConfiguration
	public static class EventStreamControllerTestConfiguration {

		@Bean
		public ObservationRegistry observationRegistry(){
			return ObservationRegistry.NOOP;
		}

		@Bean
		public FragmentRepository fragmentMongoRepository(
				final FragmentEntityRepository fragmentEntityRepository,
				final MongoTemplate mongoTemplate) {
			return new FragmentMongoRepository(fragmentEntityRepository, mongoTemplate);
		}

		@Bean
		public AllocationRepository allocationRepository(
				final AllocationEntityRepository allocationEntityRepository) {
			return new AllocationMongoRepository(allocationEntityRepository);
		}
	}

}
