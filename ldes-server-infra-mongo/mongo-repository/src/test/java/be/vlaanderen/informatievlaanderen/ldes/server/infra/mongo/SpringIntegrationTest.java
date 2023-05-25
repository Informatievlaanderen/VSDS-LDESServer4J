package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.LdesFragmentMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.repository.LdesFragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.MemberMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.repository.LdesMemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat.ServerDcatMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat.repository.ServerDcatEntityRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataMongoTest
@ActiveProfiles("mongo-test")
@ContextConfiguration(classes = { LdesMemberEntityRepository.class, LdesFragmentEntityRepository.class,
		ServerDcatEntityRepository.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member",
		"be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.membersequence",
		"be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment" })
@Import(SpringIntegrationTest.EventStreamControllerTestConfiguration.class)
@SuppressWarnings("java:S2187")
public class SpringIntegrationTest {
	@Autowired
	public ServerDcatMongoRepository serverDcatMongoRepository;
	@Autowired
	public MemberMongoRepository memberRepository;
	@Autowired
	public LdesFragmentMongoRepository ldesFragmentMongoRepository;

	@TestConfiguration
	public static class EventStreamControllerTestConfiguration {

		@Bean
		public MemberMongoRepository ldesMemberMongoRepository(
				final LdesMemberEntityRepository ldesMemberEntityRepository,
				final MongoTemplate mongoTemplate) {
			return new MemberMongoRepository(ldesMemberEntityRepository, mongoTemplate);
		}

		@Bean
		public LdesFragmentMongoRepository ldesFragmentMongoRepository(
				final LdesFragmentEntityRepository ldesFragmentEntityRepository, final MongoTemplate mongoTemplate) {
			return new LdesFragmentMongoRepository(ldesFragmentEntityRepository, mongoTemplate);
		}

		@Bean
		public ServerDcatMongoRepository serverDcatMongoRepository(
				final ServerDcatEntityRepository serverDcatEntityRepository) {
			return new ServerDcatMongoRepository(serverDcatEntityRepository);
		}
	}
}
