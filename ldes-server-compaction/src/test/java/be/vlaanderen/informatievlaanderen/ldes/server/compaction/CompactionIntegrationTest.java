package be.vlaanderen.informatievlaanderen.ldes.server.compaction;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.eventhandlers.ViewCapacityCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services.CompactionCandidateService;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services.CompactionScheduler;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services.PaginationCompactionService;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services.SchedulingConfigCompaction;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.ViewCollectionImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.spi.RetentionPolicyEmptinessChecker;
import io.cucumber.spring.CucumberContextConfiguration;
import io.micrometer.observation.ObservationRegistry;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.RecordApplicationEvents;

import static org.mockito.Mockito.mock;

@CucumberContextConfiguration
//@EnableAutoConfiguration
@RecordApplicationEvents
@ContextConfiguration(classes = {SchedulingConfigCompaction.class, CompactionScheduler.class, ViewCollectionImpl.class, PaginationCompactionService.class,
        CompactionCandidateService.class, CompactionIntegrationTest.CompactionIntegrationTestConfiguration.class, ViewCapacityCreator.class })
@ComponentScan(value = {"be.vlaanderen.informatievlaanderen.ldes.server.compaction"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {
                "be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services.FragmentationConfigCompaction"}))
@TestPropertySource(properties = "ldes-server.compaction-cron=*/4 * * * * *")
@SuppressWarnings("java:S2187")
public class CompactionIntegrationTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    PageRepository pageRepository;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;


    @TestConfiguration
    public static class CompactionIntegrationTestConfiguration {

        @Bean
        public ObservationRegistry observationRegistry() {
            return ObservationRegistry.NOOP;
        }

        @Bean
        public RetentionPolicyEmptinessChecker retentionPolicyEmptinessChecker() {
            return mock(RetentionPolicyEmptinessChecker.class);
        }
    }
}
