package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.DcatDataServicePostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.repository.DataServiceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.DcatCatalogPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.repository.DcatCatalogEntityRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataJpaTest
@ActiveProfiles("postgres-test")
@AutoConfigureEmbeddedDatabase
@ContextConfiguration(classes = { DataServiceEntityRepository.class, DcatCatalogEntityRepository.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres" })
@Import(SpringIntegrationTest.EventStreamControllerTestConfiguration.class)
@SuppressWarnings("java:S2187")
public class SpringIntegrationTest {
	@Autowired
	public DcatCatalogPostgresRepository dcatCatalogPostgresRepository;
	@Autowired
	public DcatDataServicePostgresRepository dcatViewPostgresRepository;

	@TestConfiguration
	public static class EventStreamControllerTestConfiguration {

		@Bean
		public DcatViewRepository dcatViewPostgresRepository(
				final DataServiceEntityRepository viewEntityRepository) {
			return new DcatDataServicePostgresRepository(viewEntityRepository);
		}

		@Bean
		public DcatServerRepository serverDcatRepository(
				final DcatCatalogEntityRepository dcatCatalogEntityRepository) {
			return new DcatCatalogPostgresRepository(dcatCatalogEntityRepository);
		}

	}
}
