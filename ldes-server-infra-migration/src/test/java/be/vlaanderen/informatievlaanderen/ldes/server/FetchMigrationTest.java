package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.repository.AllocationEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableAutoConfiguration()
@SpringBootTest(classes = {MongoTestConfiguration.class, PostgresTestConfiguration.class})
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
@EntityScan("be.vlaanderen.informatievlaanderen.ldes")
@SpringBatchTest
@ActiveProfiles("test")
@EnableJpaRepositories(basePackages = "be.vlaanderen.informatievlaanderen.ldes")
public class FetchMigrationTest {
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	@Qualifier("migrationMongoFetch")
	private Job job;

	@Autowired
	private AllocationEntityRepository repository;

	@Test
	public void testBatchJob() throws Exception {
		String es = "es";
		String view = "v";
		String fragment = "/%s/%s?page=1".formatted(es, view);
		String memberId = "%s/https://ex.com/1".formatted(es);
		mongoTemplate.save(new MemberAllocationEntity("%s/%s/%s".formatted(es, memberId, fragment), es, view, fragment, memberId));
		memberId = "%s/https://ex.com/2".formatted(es);
		mongoTemplate.save(new MemberAllocationEntity("%s/%s/%s".formatted(es, memberId, fragment), es, view, fragment, memberId));
		memberId = "%s/https://ex.com/3".formatted(es);
		mongoTemplate.save(new MemberAllocationEntity("%s/%s/%s".formatted(es, memberId, fragment), es, view, fragment, memberId));
		mongoTemplate.save(new MemberAllocationEntity("%s/%s/%s".formatted(es, memberId, fragment), es, view, fragment, memberId));

		jobLauncherTestUtils.setJob(job);
		jobLauncherTestUtils.launchJob();

		var entries = repository.findAll();
		assertEquals(3, entries.size());

		var lastEntry = entries.get(2);

		assertEquals(memberId, lastEntry.getMemberId());
		assertEquals(view, lastEntry.getViewName());
		assertEquals(fragment, lastEntry.getFragmentId());
		assertEquals(es, lastEntry.getCollectionName());
	}
}
