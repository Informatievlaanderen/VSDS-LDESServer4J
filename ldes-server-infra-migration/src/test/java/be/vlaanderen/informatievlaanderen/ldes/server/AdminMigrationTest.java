package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.entity.DcatDatasetEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver.entity.DcatCatalogEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape.entity.ShaclShapeEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.DataServiceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.repository.DataServiceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.repository.DcatDatasetEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.repository.DcatCatalogEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.repository.ShaclShapeEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.repository.ViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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
	@Autowired
	private DcatCatalogEntityRepository dcatCatalogEntityRepository;
	@Autowired
	private EventStreamEntityRepository eventStreamEntityRepository;
	@Autowired
	private ShaclShapeEntityRepository shaclShapeEntityRepository;
	@Autowired
	private DataServiceEntityRepository dataServiceEntityRepository;
	@Autowired
	private ViewEntityRepository viewEntityRepository;

	// Examples
	private final String exampleModelTtlString;
	private final String exampleModelNqString;
	private final Model exampleModel;
	private final String retentionPolicy;
	private final String timestampPath = "https://w3id.org/ldes#timestampPath";
	private final String versionOfPath = "https://w3id.org/ldes#versionOfPath";

	public AdminMigrationTest() throws IOException {
		retentionPolicy = Files.readString(Path.of("src/test/resources/admin/view/retentionpolicy.nq"));
		exampleModelTtlString = Files.readString(Path.of("src/test/resources/randomModel.ttl"));
		exampleModelNqString = Files.readString(Path.of("src/test/resources/randomModel.nq"));
		exampleModel = RDFParser.fromString(exampleModelTtlString).lang(Lang.TTL).toModel();
	}


	@Test
	public void testBatchJob() throws Exception {
		saveDcatDatasets();
		saveDcatCatalogs();
		saveEventStreams();
		saveShaclShapes();
		saveDataServices();
		saveViews();

		jobLauncherTestUtils.setJob(job);
		jobLauncherTestUtils.launchJob();

		verifyDcatDatasets();
		verifyDcatCatalogs();
		verifyEventStreams();
		verifyShaclShapes();
		verifyDataServices();
		verifyViews();
	}

	private void saveDcatDatasets() {
		mongoTemplate.save(new DcatDatasetEntity("collection1", exampleModelTtlString));
		mongoTemplate.save(new DcatDatasetEntity("collection2", exampleModelTtlString));
		mongoTemplate.save(new DcatDatasetEntity("collection3", exampleModelTtlString));
		mongoTemplate.save(new DcatDatasetEntity("collection3", exampleModelTtlString));
	}

	private void saveDcatCatalogs() {
		mongoTemplate.save(new DcatCatalogEntity("catalog1", exampleModelTtlString));
		mongoTemplate.save(new DcatCatalogEntity("catalog2", exampleModelTtlString));
		mongoTemplate.save(new DcatCatalogEntity("catalog3", exampleModelTtlString));
		mongoTemplate.save(new DcatCatalogEntity("catalog3", exampleModelTtlString));
	}

	private void saveEventStreams() {
		mongoTemplate.save(new EventStreamEntity("es1", timestampPath, versionOfPath, true));
		mongoTemplate.save(new EventStreamEntity("es2", timestampPath, versionOfPath, true));
		mongoTemplate.save(new EventStreamEntity("es3", timestampPath, versionOfPath, true));
		mongoTemplate.save(new EventStreamEntity("es3", timestampPath, versionOfPath, true));
	}

	private void saveShaclShapes() {
		mongoTemplate.save(new ShaclShapeEntity("shacl1", exampleModelTtlString));
		mongoTemplate.save(new ShaclShapeEntity("shacl2", exampleModelTtlString));
		mongoTemplate.save(new ShaclShapeEntity("shacl3", exampleModelTtlString));
		mongoTemplate.save(new ShaclShapeEntity("shacl3", exampleModelTtlString));
	}

	private void saveDataServices() {
		mongoTemplate.save(new DataServiceEntity("es1/view1", exampleModelNqString));
		mongoTemplate.save(new DataServiceEntity("es1/view2", exampleModelNqString));
		mongoTemplate.save(new DataServiceEntity("es1/view3", exampleModelNqString));
		mongoTemplate.save(new DataServiceEntity("es1/view3", exampleModelNqString));
	}

	private void saveViews() {
		var fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName("fragmentationConfig");
		fragmentationConfig.setConfig(Map.of("pageNumber", "1"));

		mongoTemplate.save(new ViewEntity("es1/view1", List.of(retentionPolicy), List.of(fragmentationConfig), 10));
		mongoTemplate.save(new ViewEntity("es1/view2", List.of(retentionPolicy), List.of(fragmentationConfig), 10));
		mongoTemplate.save(new ViewEntity("es1/view3", List.of(retentionPolicy), List.of(fragmentationConfig), 10));
		mongoTemplate.save(new ViewEntity("es1/view3", List.of(retentionPolicy), List.of(fragmentationConfig), 10));
	}

	private void verifyDcatDatasets() {
		var entries = dcatDatasetEntityRepository.findAll();
		assertEquals(3, entries.size());

		assertTrue(exampleModel.isIsomorphicWith(RDFParser.fromString(entries.get(0).getModel())
				.lang(Lang.TURTLE).toModel()));
	}

	private void verifyDcatCatalogs() {
		var entries = dcatCatalogEntityRepository.findAll();
		assertEquals(3, entries.size());

		assertTrue(exampleModel.isIsomorphicWith(RDFParser.fromString(entries.get(0).getDcat())
				.lang(Lang.TURTLE).toModel()));
	}

	private void verifyEventStreams() {
		var entries = eventStreamEntityRepository.findAll();
		assertEquals(3, entries.size());

		var es1 = entries.get(0);
		assertEquals("es1", es1.getId());
		assertEquals(timestampPath, es1.getTimestampPath());
		assertEquals(versionOfPath, es1.getVersionOfPath());
		assertTrue(es1.isVersionCreationEnabled());
	}

	private void verifyShaclShapes() {
		var entries = shaclShapeEntityRepository.findAll();
		assertEquals(3, entries.size());

		assertTrue(exampleModel.isIsomorphicWith(RDFParser.fromString(entries.get(0).getModel())
				.lang(Lang.TURTLE).toModel()));
	}

	private void verifyDataServices() {
		var entries = dataServiceEntityRepository.findAll();
		assertEquals(3, entries.size());

		assertTrue(exampleModel.isIsomorphicWith(RDFParser.fromString(entries.get(0).getModel())
				.lang(Lang.TURTLE).toModel()));
	}

	private void verifyViews() {
		var entries = viewEntityRepository.findAll();
		assertEquals(3, entries.size());

		var view = entries.get(0);
		assertEquals("es1/view1", view.getViewName());
		var retentionPolicyModel = RDFParser.fromString(retentionPolicy)
				.lang(Lang.NQUADS)
				.toModel();
		var persistedRetentionPolicyModel = RDFParser.fromString(view.getRetentionPolicies().get(0))
				.lang(Lang.TTL)
				.toModel();
		assertTrue(retentionPolicyModel.isIsomorphicWith(persistedRetentionPolicyModel));
		assertEquals("1", view.getFragmentations().get(0).getConfig().get("pageNumber"));
		assertEquals(10, view.getPageSize());
	}
}
