package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.entity.DcatDataServiceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.entity.EventSourceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.MemberPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

//@CucumberContextConfiguration
//@EnableAutoConfiguration
//@DataJpaTest
//@AutoConfigureEmbeddedDatabase
//@ActiveProfiles("postgres-test")
//@ContextConfiguration(classes = { MemberEntityRepository.class})
//@EntityScan(basePackageClasses = {EventStreamEntity.class, MemberEntity.class, ViewEntity.class, DcatDatasetEntity.class,
//DcatDataServiceEntity.class, EventSourceEntity.class, ShaclShapeEntity.class})
//@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.ingest"})
//@Sql(value = {"./collections.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {"./remove.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//@SuppressWarnings("java:S2187")
//public class PostgresIngestIntegrationTest {
//
//	@Autowired
//	MemberPostgresRepository memberRepository;
//
//}
