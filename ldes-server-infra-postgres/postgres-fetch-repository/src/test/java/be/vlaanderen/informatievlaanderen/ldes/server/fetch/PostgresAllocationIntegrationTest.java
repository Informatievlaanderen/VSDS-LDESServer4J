package be.vlaanderen.informatievlaanderen.ldes.server.fetch;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.repository.AllocationEntityRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataJpaTest
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("postgres-test")
@ContextConfiguration(classes = { AllocationEntityRepository.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.fetch" })
@SuppressWarnings("java:S2187")
public class PostgresAllocationIntegrationTest {

	@Autowired
	AllocationPostgresRepository allocationPostgresRepository;

	@Autowired
	AllocationEntityRepository allocationEntityRepository;

}
