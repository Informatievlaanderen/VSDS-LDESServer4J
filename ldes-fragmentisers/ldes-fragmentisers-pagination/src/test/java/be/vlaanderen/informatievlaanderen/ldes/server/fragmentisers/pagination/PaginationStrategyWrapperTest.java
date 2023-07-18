package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PaginationStrategyWrapperTest {
	private final ApplicationContext applicationContext = mock(ApplicationContext.class);
	private final FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	private PaginationStrategyWrapper paginationUpdater;

	@BeforeEach
	void setUp() {
		paginationUpdater = new PaginationStrategyWrapper();
	}

	@Test
	void when_FragmentationStrategyIsUpdated_TimebasedFragmentationStrategyIsReturned() {
		ConfigProperties properties = new ConfigProperties(Map.of("memberLimit", "5"));
		FragmentationStrategy decoratedFragmentationStrategy = paginationUpdater
				.wrapFragmentationStrategy(applicationContext, fragmentationStrategy, properties);
		assertTrue(decoratedFragmentationStrategy instanceof PaginationStrategy);
	}
}
