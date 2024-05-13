package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.FragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.SequenceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.FragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.SequenceEntityRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier.fromFragmentId;
import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableAutoConfiguration()
@SpringBootTest(classes = {MongoTestConfiguration.class, PostgresTestConfiguration.class})
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
@EntityScan("be.vlaanderen.informatievlaanderen.ldes")
@SpringBatchTest
@ActiveProfiles("test")
@EnableJpaRepositories(basePackages = "be.vlaanderen.informatievlaanderen.ldes")
public class FragmentMigrationTest {
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	@Qualifier("migrationMongoFragmentation")
	private Job job;

	@Autowired
	private FragmentEntityRepository fragmentEntityRepository;
	@Autowired
	private SequenceEntityRepository sequenceEntityRepository;

	private static final String ES = "es";
	private static final String VIEW = "v";

	@Test
	public void testBatchJob() throws Exception {
		saveFragments();
		saveSequences();

		jobLauncherTestUtils.setJob(job);
		jobLauncherTestUtils.launchJob();

		verifyFragments();
		verifySequences();
	}

	private void saveFragments() {
		LdesFragmentIdentifier fragmentId = fromFragmentId("/%s/%s".formatted(ES, VIEW));
		mongoTemplate.save(fragmentEntity(fragmentId));
		fragmentId = fromFragmentId("/%s/%s?foo=bar".formatted(ES, VIEW));
		mongoTemplate.save(fragmentEntity(fragmentId));
		fragmentId = fromFragmentId("/%s/%s?foo=pub".formatted(ES, VIEW));
		mongoTemplate.save(fragmentEntity(fragmentId));
		mongoTemplate.save(fragmentEntity(fragmentId));
	}

	private void saveSequences() {
		mongoTemplate.save(new SequenceEntity("/es/v1", 1));
		mongoTemplate.save(new SequenceEntity("/es/v2", 1));
		mongoTemplate.save(new SequenceEntity("/es/v3", 1));
		mongoTemplate.save(new SequenceEntity("/es/v3", 1));
	}

	private void verifyFragments() {
		var entries = fragmentEntityRepository.findAll();

		assertEquals(3, entries.size());

		var entry = entries.get(0);
		assertEquals("/%s/%s".formatted(ES, VIEW), entry.getId());
		assertEquals(fromFragmentId("/%s/%s".formatted(ES, VIEW)).getViewName().asString(), entry.getViewName());

		var relation = entry.getRelations().get(0);
		assertEquals(exampleRelation().relation(), relation.getRelation());
		assertEquals(exampleRelation().treeNode().toString(), relation.getTreeNode());
		assertEquals(exampleRelation().treePath(), relation.getTreePath());
		assertEquals(exampleRelation().treeValue(), relation.getTreeValue());
		assertEquals(exampleRelation().treeValueType(), relation.getTreeValueType());
	}

	private void verifySequences() {
		var entries = sequenceEntityRepository.findAll();
		assertEquals(3, entries.size());

		assertEquals(1, entries.get(0).getLastProcessedSequence());
		assertEquals("/es/v1", entries.get(0).getViewName());
	}

	private FragmentEntity fragmentEntity(LdesFragmentIdentifier fragmentId) {
		return new FragmentEntity(fragmentId.toString(), true,
				fragmentId.getViewName().asString(), fragmentId.getFragmentPairs(),
				false, fragmentId.getParentId().toString(), 0,
				List.of(exampleRelation()), fragmentId.getViewName().getCollectionName(),
				LocalDateTime.now(), LocalDateTime.now());
	}

	private TreeRelation exampleRelation() {
		return new TreeRelation("path", fromFragmentId("/%s/%s".formatted(ES, VIEW)), "value", "valueType", "relation");
	}
}
