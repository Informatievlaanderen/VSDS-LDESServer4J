package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentationMetricsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.MemberMetricsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.ServerMetrics;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.MemberPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import static org.mockito.Mockito.mock;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataJpaTest
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("postgres-test")
@EntityScan(basePackages = {"be.vlaanderen.informatievlaanderen.ldes.server"})
@ComponentScan(basePackages = {"be.vlaanderen.informatievlaanderen.ldes.server.ingest",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream"})
@ContextConfiguration(classes = {MemberEntityRepository.class})
@EnableJpaRepositories(basePackageClasses = {MemberEntityRepository.class, EventStreamEntityRepository.class})
@Import(PostgresIngestIntegrationTest.IngestTestConfiguration.class)
@Sql(value = {"init-collections.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = "DELETE FROM collections;", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SuppressWarnings("java:S2187")
public class PostgresIngestIntegrationTest {

	@Autowired
	MemberPostgresRepository memberRepository;
	@Autowired
	EventStreamRepository eventStreamRepository;

	@TestConfiguration
	static class IngestTestConfiguration {
		@Bean
		ServerMetrics serverMetrics() {
			return new ServerMetrics(mock(FragmentationMetricsRepository.class), mock(MemberMetricsRepository.class));
		}
	}
}
