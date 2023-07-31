package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.FragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.mock;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataMongoTest
@ActiveProfiles("mongo-test")
@ContextConfiguration(classes = { FragmentEntityRepository.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.fragmentation",
		"be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo" })
@Import(MongoFragmentationIntegrationTest.EventStreamControllerTestConfiguration.class)
@SuppressWarnings("java:S2187")
public class MongoFragmentationIntegrationTest {

	@Autowired
	public FragmentRepository fragmentRepository;

	@TestConfiguration
	public static class EventStreamControllerTestConfiguration {

		@Bean
		public ObservationRegistry observationRegistry() {
			return ObservationRegistry.NOOP;
		}

		@Bean
		public MemberRepository memberRepository() {
			return mock(MemberRepository.class);
		}

		@Bean
		public DcatDatasetRepository dcatDatasetMongoRepository() {
			return mock(DcatDatasetRepository.class);
		}

		@Bean
		public DcatServerRepository dcatServerRepository() {
			return mock(DcatServerRepository.class);
		}

		@Bean
		public DcatViewRepository dcatViewRepository() {
			return mock(DcatViewRepository.class);
		}

		@Bean
		public EventStreamRepository eventStreamRepository() {
			return mock(EventStreamRepository.class);
		}

		@Bean
		public ViewRepository viewRepository() {
			return mock(ViewRepository.class);
		}

		@Bean
		public ShaclShapeRepository shaclShapeRepository() {
			return mock(ShaclShapeRepository.class);
		}

	}

}
