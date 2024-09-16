package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.repository.ViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.BucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataJpaTest
@AutoConfigureEmbeddedDatabase(refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("postgres-test")
@EntityScan(basePackages = {"be.vlaanderen.informatievlaanderen.ldes.server"})
@ComponentScan(basePackages = {"be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres",
		"be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres",
		"be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres",
//		"be.vlaanderen.informatievlaanderen.ldes.server.domain",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream"
})
@ContextConfiguration(classes = {PageEntityRepository.class})
@EnableJpaRepositories(basePackageClasses = {PageEntityRepository.class, PageMemberEntityRepository.class, PageRelationEntityRepository.class,
MemberEntityRepository.class, BucketEntityRepository.class, ViewEntityRepository.class, EventStreamEntityRepository.class})
@Sql(value = {"init-paged-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

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

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	MemberEntityRepository memberRepository;
	@Autowired
	BucketEntityRepository bucketEntityRepository;

	@Autowired
	EventStreamEntityRepository eventStreamEntityRepository;
	@Autowired
	ViewEntityRepository viewEntityRepository;

}
