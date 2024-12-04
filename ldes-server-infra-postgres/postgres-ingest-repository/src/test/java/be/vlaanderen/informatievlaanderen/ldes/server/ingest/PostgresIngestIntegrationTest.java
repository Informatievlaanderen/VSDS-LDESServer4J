package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.metrics.IngestionMetricsService;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.MemberPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataJpaTest
@AutoConfigureEmbeddedDatabase(type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
@ActiveProfiles("postgres-test")
@EntityScan(basePackages = {"be.vlaanderen.informatievlaanderen.ldes.server"})
@ComponentScan(basePackages = {"be.vlaanderen.informatievlaanderen.ldes.server.ingest",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream"})
@ContextConfiguration(classes = {MemberEntityRepository.class})
@EnableJpaRepositories(basePackageClasses = {MemberEntityRepository.class, EventStreamEntityRepository.class})
@Sql(value = {"init-collections.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = "DELETE FROM collections;", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Import(BuildProperties.class)
@SuppressWarnings("java:S2187")
public class PostgresIngestIntegrationTest {

	@Autowired
	MemberPostgresRepository memberRepository;
	@Autowired
	EventStreamRepository eventStreamRepository;
	@MockBean
	IngestionMetricsService metricsService;
}
