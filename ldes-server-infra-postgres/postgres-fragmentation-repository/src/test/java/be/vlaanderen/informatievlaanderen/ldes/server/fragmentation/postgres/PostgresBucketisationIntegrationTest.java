package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentationMetricsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.FragmentationMetricsService;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.delegates.BucketisedMemberItemWriterConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.micrometer.observation.ObservationRegistry;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.mock;

@CucumberContextConfiguration
@EnableAutoConfiguration(exclude = FragmentationService.class)
@DataJpaTest
@AutoConfigureEmbeddedDatabase(refresh = AutoConfigureEmbeddedDatabase.RefreshMode.BEFORE_EACH_TEST_METHOD)
@EnableBatchProcessing
@ActiveProfiles("postgres-test")
@EntityScan(basePackages = {"be.vlaanderen.informatievlaanderen.ldes.server"})
@ComponentScan(basePackages = {
		"be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain"
})
@EnableJpaRepositories(basePackages = {
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view",
		"be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres"
})
@ContextConfiguration(classes = {BucketisedMemberItemWriterConfig.class})
@Import({PostgresBucketisationIntegrationTest.EventStreamControllerTestConfiguration.class, BuildProperties.class})
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
		FragmentationMetricsService serverMetrics() {
			return new FragmentationMetricsService(mock(FragmentationMetricsRepository.class));
		}
	}
}
