package be.vlaanderen.informatievlaanderen.ldes.server.compaction;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services.SchedulingConfigCompaction;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.BulkMemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.spi.RetentionPolicyEmptinessChecker;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.RecordApplicationEvents;

import static org.mockito.Mockito.mock;

@CucumberContextConfiguration
@EnableAutoConfiguration
@RecordApplicationEvents
@ContextConfiguration(classes = {SchedulingConfigCompaction.class})
@ComponentScan(value = {"be.vlaanderen.informatievlaanderen.ldes.server.compaction"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {
                "be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services.FragmentationConfigCompaction"}))
@TestPropertySource(properties = "ldes-server.compaction-cron=*/4 * * * * *")
@SuppressWarnings("java:S2187")
public class CompactionIntegrationTest {

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    @MockBean
    EventConsumer eventConsumer;
    @Autowired
    @MockBean
    FragmentRepository fragmentRepository;
    @Autowired
    @MockBean
    AllocationRepository allocationRepository;

    @TestComponent
    protected static class EventConsumer {
        @EventListener
        public void consumeEvent(BulkMemberAllocatedEvent testEvent) {
        }
    }

    @TestConfiguration
    public static class CompactionIntegrationTestConfiguration {

        @Bean
        public RetentionPolicyEmptinessChecker retentionPolicyEmptinessChecker() {
            return mock(RetentionPolicyEmptinessChecker.class);
        }
    }
}
