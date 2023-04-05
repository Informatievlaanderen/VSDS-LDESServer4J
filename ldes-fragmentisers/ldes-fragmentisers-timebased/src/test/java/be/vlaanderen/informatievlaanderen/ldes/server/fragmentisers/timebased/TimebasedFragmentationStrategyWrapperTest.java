package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TimebasedFragmentationStrategyWrapperTest {

	private final ApplicationContext applicationContext = mock(ApplicationContext.class);
	private final FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	private TimebasedFragmentationStrategyWrapper timebasedFragmentationUpdater;

	@BeforeEach
	void setUp() {
		timebasedFragmentationUpdater = new TimebasedFragmentationStrategyWrapper();
	}

	@Test
	void when_FragmentationStrategyIsUpdated_TimebasedFragmentationStrategyIsReturned() {
		FragmentationProperties properties = new FragmentationProperties(Map.of("memberLimit", "5"));
		FragmentationStrategy decoratedFragmentationStrategy = timebasedFragmentationUpdater
				.wrapFragmentationStrategy(applicationContext, fragmentationStrategy, properties);
		assertTrue(decoratedFragmentationStrategy instanceof TimebasedFragmentationStrategy);
	}
}