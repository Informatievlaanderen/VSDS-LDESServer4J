package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableAutoConfiguration()
@SpringBootTest(classes = {MongoTestConfiguration.class, PostgresTestConfiguration.class})
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
@EntityScan("be.vlaanderen.informatievlaanderen.ldes")
@SpringBatchTest
@ActiveProfiles("test")
@EnableJpaRepositories(basePackages = "be.vlaanderen.informatievlaanderen.ldes")
class IngestMigrationTest {
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	@Qualifier("migrationMongoIngest")
	private Job job;

	@Autowired
	private MemberEntityRepository repository;

	private final String exampleModelNqString;
	private final Model exampleModel;

	public IngestMigrationTest() throws IOException {
		this.exampleModelNqString = Files.readString(Path.of("src/test/resources/randomModel.nq"));
		this.exampleModel = RDFParser.fromString(exampleModelNqString)
				.lang(Lang.NQUADS)
				.toModel();

	}

	@Test
	void testBatchJob() throws Exception {
		String es = "es";
		String memberId = "%s/https://ex.com/1".formatted(es);
		String versionOf = "https://example.com/John-Doe";
		LocalDateTime timestamp = LocalDateTime.parse("2024-05-14T11:08:30.217");
		mongoTemplate.save(new MemberEntity(memberId, es, versionOf, timestamp, 1L, "1", exampleModelNqString));
		memberId = "%s/https://ex.com/2".formatted(es);
		mongoTemplate.save(new MemberEntity(memberId, es, versionOf, timestamp, 2L, "1", exampleModelNqString));
		memberId = "%s/https://ex.com/3".formatted(es);
		mongoTemplate.save(new MemberEntity(memberId, es, versionOf, timestamp, 3L, "1", exampleModelNqString));
		mongoTemplate.save(new MemberEntity(memberId, es, versionOf, timestamp, 3L, "1", exampleModelNqString));

		jobLauncherTestUtils.setJob(job);
		jobLauncherTestUtils.launchJob();

		var entries = repository.findAll();
		assertEquals(3, entries.size());

		var lastEntry = entries.get(2);

		assertEquals(memberId, lastEntry.getId());
		assertEquals(es, lastEntry.getCollectionName());
		assertEquals("1", lastEntry.getTransactionId());
		assertEquals(timestamp, lastEntry.getTimestamp());
		assertEquals(versionOf, lastEntry.getVersionOf());
		assertEquals(3L, lastEntry.getSequenceNr());

		ByteArrayInputStream inputStream = new ByteArrayInputStream(lastEntry.getModel());
		Model returnedModel = RDFParser.source(inputStream).lang(Lang.RDFPROTO).toModel();

		assertTrue(exampleModel.isIsomorphicWith(returnedModel));
	}
}
