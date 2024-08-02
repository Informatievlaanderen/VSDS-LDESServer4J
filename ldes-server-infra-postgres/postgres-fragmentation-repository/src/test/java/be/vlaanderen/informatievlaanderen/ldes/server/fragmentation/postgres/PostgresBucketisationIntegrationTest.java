package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.BucketisedMemberWriter;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.micrometer.observation.ObservationRegistry;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.mock;

@CucumberContextConfiguration
@EnableAutoConfiguration(exclude = FragmentationService.class)
@DataJpaTest
@AutoConfigureEmbeddedDatabase
@EnableBatchProcessing
@ActiveProfiles("postgres-test")
@EntityScan(basePackages = {"be.vlaanderen.informatievlaanderen.ldes.server"})
@ComponentScan(basePackages = {
		"be.vlaanderen.informatievlaanderen.ldes.server.fragmentation",
		"be.vlaanderen.informatievlaanderen.ldes.server.ingest",
		"be.vlaanderen.informatievlaanderen.ldes.server.retention",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain"
})
@ContextConfiguration(classes = {BucketisedMemberWriter.class})
@Import(PostgresBucketisationIntegrationTest.EventStreamControllerTestConfiguration.class)
@SuppressWarnings("java:S2187")
public class PostgresBucketisationIntegrationTest {
	@MockBean
	public MemberEntityRepository memberEntityRepository;

	@TestConfiguration
	public static class EventStreamControllerTestConfiguration {

		@Bean
		public ObservationRegistry observationRegistry() {
			return ObservationRegistry.NOOP;
		}

		@Bean
		public MemberPropertiesRepository memberPropertiesRepository() {
			return mock(MemberPropertiesRepository.class);
		}
	}
}
