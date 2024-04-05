package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver.DcatServerMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver.repository.DcatCatalogEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.DcatViewMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.repository.DataServiceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.service.DcatServiceEntityConverter;
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
@ContextConfiguration(classes = { DataServiceEntityRepository.class, DcatCatalogEntityRepository.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member",
		"be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.membersequence",
		"be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation" })
@Import(SpringIntegrationTest.EventStreamControllerTestConfiguration.class)
@SuppressWarnings("java:S2187")
public class SpringIntegrationTest {
	@Autowired
	public DcatServerMongoRepository serverDcatMongoRepository;
	@Autowired
	public DcatViewMongoRepository dcatViewMongoRepository;
	@Autowired
	public MongoTemplate mongoTemplate;

	@TestConfiguration
	public static class EventStreamControllerTestConfiguration {

		@Bean
		public DcatViewRepository dcatViewMongoRepository(
				final DataServiceEntityRepository dataServiceEntityRepository, final MongoTemplate mongoTemplate) {
			return new DcatViewMongoRepository(dataServiceEntityRepository, new DcatServiceEntityConverter(), mongoTemplate);
		}

		@Bean
		public DcatServerRepository serverDcatRepository(
				final DcatCatalogEntityRepository dcatCatalogEntityRepository) {
			return new DcatServerMongoRepository(dcatCatalogEntityRepository);
		}

	}
}
