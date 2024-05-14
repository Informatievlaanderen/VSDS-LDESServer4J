package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.repository.MemberPropertiesEntityRepository;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableAutoConfiguration()
@SpringBootTest(classes = {MongoTestConfiguration.class, PostgresTestConfiguration.class})
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
@EntityScan("be.vlaanderen.informatievlaanderen.ldes")
@SpringBatchTest
@ActiveProfiles("test")
public class RetentionMigrationTest {
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	@Qualifier("migrationMongoRetention")
	private Job job;

	@Autowired
	private MemberPropertiesEntityRepository repository;

	@Test
	public void testBatchJob() throws Exception {
		String es = "es";
		String memberId = "%s/https://ex.com/1".formatted(es);
		String versionOf = "https://example.com/John-Doe";
		LocalDateTime timestamp = LocalDateTime.parse("2024-05-14T11:08:30.217");
		mongoTemplate.save(new MemberPropertiesEntity(memberId, es, Set.of("v1", "v2"), versionOf, timestamp));
		memberId = "%s/https://ex.com/2".formatted(es);
		mongoTemplate.save(new MemberPropertiesEntity(memberId, es, Set.of("v1", "v2"), versionOf, timestamp));
		memberId = "%s/https://ex.com/3".formatted(es);
		mongoTemplate.save(new MemberPropertiesEntity(memberId, es, Set.of("v1", "v2"), versionOf, timestamp));
		mongoTemplate.save(new MemberPropertiesEntity(memberId, es, Set.of("v1", "v2"), versionOf, timestamp));

		jobLauncherTestUtils.setJob(job);
		jobLauncherTestUtils.launchJob();

		var entries = repository.findAll();
		assertEquals(3, entries.size());

		var lastEntry = entries.get(2);

		assertEquals(memberId, lastEntry.getId());
		assertEquals(es, lastEntry.getCollectionName());
		assertEquals(timestamp, lastEntry.getTimestamp());
		assertEquals(versionOf, lastEntry.getVersionOf());
		assertTrue(lastEntry.getViews().containsAll(List.of("v1", "v2")));
	}
}
