package be.vlaanderen.informatievlaanderen.ldes.server.compaction;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services.SchedulingConfigCompaction;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.RecordApplicationEvents;

@CucumberContextConfiguration
@EnableAutoConfiguration
@RecordApplicationEvents
@ContextConfiguration(classes = { SchedulingConfigCompaction.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.compaction" })
@SuppressWarnings("java:S2187")
public class CompactionIntegrationTest {

	@Autowired
	ApplicationEventPublisher applicationEventPublisher;
}
