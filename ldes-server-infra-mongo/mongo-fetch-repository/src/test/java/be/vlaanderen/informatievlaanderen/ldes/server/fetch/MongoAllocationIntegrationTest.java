package be.vlaanderen.informatievlaanderen.ldes.server.fetch;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchmongo.AllocationMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchmongo.repository.AllocationEntityRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataMongoTest
@ActiveProfiles("mongo-test")
@ContextConfiguration(classes = { AllocationEntityRepository.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.fetch" })
@SuppressWarnings("java:S2187")
public class MongoAllocationIntegrationTest {

	@Autowired
    AllocationMongoRepository allocationMongoRepository;

	@Autowired
	AllocationEntityRepository allocationEntityRepository;

}
