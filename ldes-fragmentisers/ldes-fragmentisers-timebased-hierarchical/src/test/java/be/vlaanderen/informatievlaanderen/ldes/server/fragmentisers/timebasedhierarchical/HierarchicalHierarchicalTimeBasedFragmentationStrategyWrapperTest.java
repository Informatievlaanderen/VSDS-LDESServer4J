package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class HierarchicalHierarchicalTimeBasedFragmentationStrategyWrapperTest {
	private final ApplicationContext applicationContext = mock(ApplicationContext.class);
	private final FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	private HierarchicalTimeBasedFragmentationStrategyWrapper fragmentationStrategyWrapper;

	@BeforeEach
	void setUp() {
		fragmentationStrategyWrapper = new HierarchicalTimeBasedFragmentationStrategyWrapper();
	}

	@Test
	void when_FragmentationStrategyIsUpdated_TimebasedFragmentationStrategyIsReturned() {
		ConfigProperties properties = new ConfigProperties(
				Map.of("maxGranularity", Granularity.SECOND.getValue(), "fragmentationPath",
						"http://www.w3.org/ns/prov#generatedAtTime"));
		FragmentationStrategy decoratedFragmentationStrategy = fragmentationStrategyWrapper
				.wrapFragmentationStrategy(applicationContext, fragmentationStrategy, properties);
		assertTrue(decoratedFragmentationStrategy instanceof HierarchicalTimeBasedFragmentationStrategy);
	}

}