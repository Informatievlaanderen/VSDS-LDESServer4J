package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.BucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch.PageReader;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageEntityRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureObservability
@CucumberContextConfiguration
@EnableAutoConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase(type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
		refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD,
		replace = AutoConfigureEmbeddedDatabase.Replace.ANY)
@ActiveProfiles("postgres-test")
@ContextConfiguration(classes = {MemberEntityRepository.class})
@ComponentScan(value = {"be.vlaanderen.informatievlaanderen.ldes.server"})
@SuppressWarnings("java:S2187")
public class LdesServerIntegrationTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	PageEntityRepository pageEntityRepository;

	@Autowired
	BucketEntityRepository bucketEntityRepository;

}
