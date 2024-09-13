package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageMemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageRelationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataJpaTest
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("postgres-test")
@EntityScan(basePackages = {"be.vlaanderen.informatievlaanderen.ldes.server"})
@ComponentScan(basePackages = {"be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres",
//		"be.vlaanderen.informatievlaanderen.ldes.server.domain",
//		"be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream"
})
@ContextConfiguration(classes = {PageEntityRepository.class})
@EnableJpaRepositories(basePackageClasses = {PageEntityRepository.class, PageMemberEntityRepository.class, PageRelationEntityRepository.class})

@SuppressWarnings("java:S2187")
public class PostgresPaginationIntegrationTest {

	@Autowired
	PageRepository pageRepository;
	@Autowired
	PageMemberRepository pageMemberRepository;

	@Autowired
	PageEntityRepository pageEntityRepository;
	@Autowired
	PageMemberEntityRepository pageMemberEntityRepository;
	@Autowired
	PageRelationEntityRepository pageRelationEntityRepository;

}
