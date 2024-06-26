package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.FragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.MemberBucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PaginationSequenceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.micrometer.observation.ObservationRegistry;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataJpaTest
@AutoConfigureEmbeddedDatabase
@ContextConfiguration(classes = {ApplicationContext.class})
@ActiveProfiles("postgres-test")
@ComponentScan(value = {"be.vlaanderen.informatievlaanderen.ldes.server"})
@Import(PostgresPaginationIntegrationTest.PaginationTestConfiguration.class)
@SuppressWarnings("java:S2187")
public class PostgresPaginationIntegrationTest {

    @Autowired
    public PaginationSequenceRepository sequenceRepository;

    @TestConfiguration
    public static class PaginationTestConfiguration {

        @Bean
        public ObservationRegistry observationRegistry() {
            return ObservationRegistry.NOOP;
        }

        @Bean
        public MemberBucketEntityRepository memberBucketRepository() {
            return Mockito.mock(MemberBucketEntityRepository.class);
        }

        @Bean
        public FragmentEntityRepository fragmentEntityRepositoryRepository() {
            return Mockito.mock(FragmentEntityRepository.class);
        }

        @Bean
        public MemberRepository memberRepository() {
            return Mockito.mock(MemberRepository.class);
        }

        @Bean
        public MemberPropertiesRepository memberPropertiesRepository() {
            return Mockito.mock(MemberPropertiesRepository.class);
        }

    }
}
