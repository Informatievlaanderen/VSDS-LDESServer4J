package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
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
		refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("postgres-test")
@ContextConfiguration(classes = { MemberEntityRepository.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server" })
@SuppressWarnings("java:S2187")
public class LdesServerIntegrationTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	MemberRepository memberRepository;

}
