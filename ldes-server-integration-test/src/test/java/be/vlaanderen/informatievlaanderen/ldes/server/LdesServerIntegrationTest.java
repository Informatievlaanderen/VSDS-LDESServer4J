package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.UnprocessedViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.PageRelationPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageMemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageRelationEntityRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityManager;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.*;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@AutoConfigureObservability
@CucumberContextConfiguration
@EnableAutoConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase(type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
		replace = AutoConfigureEmbeddedDatabase.Replace.ANY)
@ActiveProfiles("postgres-test")
@ContextConfiguration(classes = {MemberEntityRepository.class, PageRelationPostgresRepository.class, PageRelationEntityRepository.class, PageMemberEntityRepository.class})
@ComponentScan(value = {"be.vlaanderen.informatievlaanderen.ldes.server"})
@Import({BuildProperties.class})
@TestPropertySource(properties = {
		"ldes-server.fragmentation-cron=*/1 * * * * *",
		"ldes-server.maintenance-cron=*/10 * * * * *",
		"ldes-server.compaction-duration=PT1S"
})
@Testcontainers
@SuppressWarnings("java:S2187")
public class LdesServerIntegrationTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	EntityManager entityManager;
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	UnprocessedViewRepository unprocessedViewRepository;
	@Autowired
	PageRelationEntityRepository pageRelationEntityRepository;
	@Autowired
	PageEntityRepository pageEntityRepository;
	@Autowired
	PageMemberEntityRepository pageMemberEntityRepository;

	@Autowired
	DataSource dataSource;

	@Autowired
	JobExplorer jobExplorer;

	@Container
	static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
			.withKraft();

	@DynamicPropertySource
	static void overrideProperties(DynamicPropertyRegistry registry) {
		kafka.start();
		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
	}
}
