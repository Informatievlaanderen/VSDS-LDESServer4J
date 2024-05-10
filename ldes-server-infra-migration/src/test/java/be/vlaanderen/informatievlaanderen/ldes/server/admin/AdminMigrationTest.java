package be.vlaanderen.informatievlaanderen.ldes.server.admin;

import be.vlaanderen.informatievlaanderen.ldes.server.MongoTestConfiguration;
import be.vlaanderen.informatievlaanderen.ldes.server.PostgresTestConfiguration;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.entity.DcatDatasetEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.repository.DcatDatasetEntityRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableAutoConfiguration()
@SpringBootTest(classes = {MongoTestConfiguration.class, PostgresTestConfiguration.class})
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
@EntityScan("be.vlaanderen.informatievlaanderen.ldes")
@SpringBatchTest
@ActiveProfiles("test")
@EnableJpaRepositories(basePackages = "be.vlaanderen.informatievlaanderen.ldes")
public class AdminMigrationTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	@Qualifier("migrationMongoAdmin")
	private Job job;

	@Autowired
	private DcatDatasetEntityRepository dcatDatasetEntityRepository;

	// Examples

	private Model dcatDatasetModel;


	@Test
	public void testBatchJob() throws Exception {
		saveDcatDatasets();

		jobLauncherTestUtils.setJob(job);
		jobLauncherTestUtils.launchJob();

		verifyDcatDatasets();
	}

	private void saveDcatDatasets() {
		String modelContent = """
				@prefix dcat: <http://www.w3.org/ns/dcat#> .
				@prefix dct:  <http://purl.org/dc/terms/> .
				    
				[ a                dcat:Dataset ;
				  dct:description  "LDES for my buildings data collection"@en ;
				  dct:title        "My LDES"@en
				] .
					""";
		dcatDatasetModel = RDFParser.fromString(modelContent).lang(Lang.TTL).toModel();

		mongoTemplate.save(new DcatDatasetEntity("collection1", modelContent));
		mongoTemplate.save(new DcatDatasetEntity("collection2", modelContent));
		mongoTemplate.save(new DcatDatasetEntity("collection3", modelContent));
	}

	private void verifyDcatDatasets() {
		var entries = dcatDatasetEntityRepository.findAll();
		assertEquals(3, entries.size());

		assertTrue(dcatDatasetModel.isIsomorphicWith(RDFParser.fromString(entries.get(0).getModel())
				.lang(Lang.TURTLE).toModel()));
	}
}
