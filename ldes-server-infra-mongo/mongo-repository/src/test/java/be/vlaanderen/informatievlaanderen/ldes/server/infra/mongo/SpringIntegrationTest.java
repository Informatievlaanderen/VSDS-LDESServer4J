package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.LdesFragmentMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.MemberMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.repository.LdesMemberEntityRepository;
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
@ContextConfiguration(classes = { LdesMemberEntityRepository.class, LdesFragmentMongoRepository.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member",
		"be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.membersequence",
		"be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment" })
@SuppressWarnings("java:S2187")
public class SpringIntegrationTest {

	@Autowired
	public MemberMongoRepository memberRepository;
	@Autowired
	public LdesFragmentMongoRepository ldesFragmentMongoRepository;
}
