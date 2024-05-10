package be.vlaanderen.informatievlaanderen.ldes.server.postgres.fetch;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.AllocationPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.repository.AllocationEntityRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataJpaTest
@AutoConfigureEmbeddedDatabase(refresh = AFTER_EACH_TEST_METHOD)
@ActiveProfiles("postgres-test")
@ContextConfiguration(classes = { AllocationEntityRepository.class, AllocationPostgresRepository.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.fetch" })
@SuppressWarnings("java:S2187")
public class PostgresAllocationIntegrationTest {

	@Autowired
	AllocationPostgresRepository allocationPostgresRepository;

	@Autowired
	AllocationEntityRepository allocationEntityRepository;

}
