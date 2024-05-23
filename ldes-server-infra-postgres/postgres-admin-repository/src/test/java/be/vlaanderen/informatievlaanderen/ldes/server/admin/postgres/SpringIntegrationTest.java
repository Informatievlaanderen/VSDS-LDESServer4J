package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.repository.DataServiceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.DcatCatalogPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.repository.DcatCatalogEntityRepository;
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
@ActiveProfiles("postgres-test")
@AutoConfigureEmbeddedDatabase
@ContextConfiguration(classes = { DataServiceEntityRepository.class, DcatCatalogEntityRepository.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres" })
@SuppressWarnings("java:S2187")
public class SpringIntegrationTest {
	@Autowired
	public DcatCatalogPostgresRepository dcatCatalogPostgresRepository;
	@Autowired
	public DcatViewRepository dcatViewPostgresRepository;
}
