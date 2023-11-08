package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.RecordApplicationEvents;

@CucumberContextConfiguration
@EnableAutoConfiguration
@RecordApplicationEvents
@ContextConfiguration(classes = {SchedulingConfigFragmentation.class, FragmentDeletionScheduler.class})
@TestPropertySource(properties = "ldes-server.deletion-cron=*/4 * * * * *")
@SuppressWarnings("java:S2187")
public class FragmentDeletionIntegrationTest {
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	@MockBean
	FragmentRepository fragmentRepository;

}
