package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.PageRelationPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.RelationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.services.BucketRelationsEventListener;
import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

@AutoConfigureObservability
@CucumberContextConfiguration
@EnableAutoConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase(type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
		refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD,
		replace = AutoConfigureEmbeddedDatabase.Replace.ANY)
@ActiveProfiles("postgres-test")
@ContextConfiguration(classes = { MemberEntityRepository.class, PageRelationPostgresRepository.class, RelationEntityRepository.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server" })
@TestPropertySource(properties = { "ldes-server.fragmentation-cron=*/1 * * * * *", "ldes-server.compaction-cron=0 * * * * *" })
@SuppressWarnings("java:S2187")
public class LdesServerIntegrationTest {
	static final int FRAGMENTATION_POLLING_RATE = 1000;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	EntityManager entityManager;
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	DataSource dataSource;

}
