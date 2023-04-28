package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

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
@ContextConfiguration(classes = { MemberEntityRepository.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.ingest",
		"be.vlaanderen.informatievlaanderen.ldes.server.ingest.membersequence" })
@SuppressWarnings("java:S2187")
public class SpringIntegrationTest {

	@Autowired
	MemberRepositoryImpl memberRepository;

}
