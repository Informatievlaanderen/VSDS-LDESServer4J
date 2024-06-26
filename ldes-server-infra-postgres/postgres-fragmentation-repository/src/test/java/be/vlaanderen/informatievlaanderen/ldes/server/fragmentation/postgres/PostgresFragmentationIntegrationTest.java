package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.BucketisationPartitioner;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.BucketisedMemberWriter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.ViewBucketisationPartitioner;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.bucketisedmember.MemberBucketJpaRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.MemberBucketEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.FragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.MemberBucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.micrometer.observation.ObservationRegistry;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.mock;

@CucumberContextConfiguration
@EnableAutoConfiguration(exclude = FragmentationService.class)
@DataJpaTest
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("postgres-test")
@ContextConfiguration(classes = {FragmentPostgresRepository.class, FragmentEntityRepository.class, BucketisedMemberPostgresRepository.class,
        MemberBucketEntityMapper.class, MemberBucketJpaRepository.class, MemberBucketEntityRepository.class,
        BucketisationPartitioner.class, ViewBucketisationPartitioner.class, BucketisedMemberWriter.class})
@Import(PostgresFragmentationIntegrationTest.EventStreamControllerTestConfiguration.class)
@SuppressWarnings("java:S2187")
public class PostgresFragmentationIntegrationTest {

    @Autowired
    public FragmentRepository fragmentRepository;

    @Autowired
    public BucketisedMemberRepository bucketisedMemberRepository;

    @Autowired
    public MemberBucketEntityMapper mapper;

    @Autowired
    public MemberBucketEntityRepository memberBucketEntityRepository;

    @Autowired
    public MemberBucketJpaRepository memberBucketJpaRepository;

    @TestConfiguration
    public static class EventStreamControllerTestConfiguration {

        @Bean
        public ObservationRegistry observationRegistry() {
            return ObservationRegistry.NOOP;
        }

        @Bean
        public MemberRepository memberRepository() {
            return mock(MemberRepository.class);
        }

        @Bean
        public MemberPropertiesRepository memberPropertiesRepository() {
            return mock(MemberPropertiesRepository.class);
        }
    }
}
