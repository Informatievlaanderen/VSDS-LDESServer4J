package be.vlaanderen.informatievlaanderen.ldes.server.retention.integrationtest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.integrationtest.stub.InMemoryMemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution.SchedulingConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.RecordApplicationEvents;

@CucumberContextConfiguration
@EnableAutoConfiguration
@RecordApplicationEvents
@ContextConfiguration(classes = {InMemoryMemberPropertiesRepository.class, SchedulingConfig.class, ConfigDataApplicationContextInitializer.class})
@ComponentScan(value = {"be.vlaanderen.informatievlaanderen.ldes.server.retention"})
@EnableConfigurationProperties(value = ServerConfig.class)
@TestPropertySource(properties = "ldes-server.retention-cron=*/4 * * * * *")
@SuppressWarnings("java:S2187")
public class RetentionIntegrationTest {

	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	MemberPropertiesRepository memberPropertiesRepository;
}
