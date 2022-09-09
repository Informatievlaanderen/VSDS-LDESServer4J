package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.FragmentationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TimebasedFragmentationUpdaterTest {

	private final ApplicationContext applicationContext = mock(ApplicationContext.class);
	private final FragmentationService fragmentationService = mock(FragmentationService.class);
	private TimebasedFragmentationUpdater timebasedFragmentationUpdater;

	@BeforeEach
	void setUp() {
		timebasedFragmentationUpdater = new TimebasedFragmentationUpdater();
	}

	@Test
	void when_FragmentationServiceIsUpdated_TimebasedFragmentationServiceIsReturned() {
		FragmentationProperties properties = new FragmentationProperties(Map.of("memberLimit", "5"));
		FragmentationService decoratedFragmentationService = timebasedFragmentationUpdater
				.updateFragmentationService(applicationContext, fragmentationService, properties);
		assertTrue(decoratedFragmentationService instanceof TimebasedFragmentationService);
	}
}