package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class SubstringFragmentationStrategyWrapperTest {

	private final ApplicationContext applicationContext = mock(ApplicationContext.class);
	private final FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	private SubstringFragmentationStrategyWrapper substringFragmentationStrategyWrapper;

	@BeforeEach
	void setUp() {
		substringFragmentationStrategyWrapper = new SubstringFragmentationStrategyWrapper();
	}

	@Test
	void when_FragmentationStrategyIsUpdated_TimebasedFragmentationStrategyIsReturned() {
		FragmentationProperties properties = new FragmentationProperties(
				Map.of("memberLimit", "5", "fragmenterProperty", "http://purl.org/dc/terms/description"));
		FragmentationStrategy decoratedFragmentationStrategy = substringFragmentationStrategyWrapper
				.wrapFragmentationStrategy(applicationContext, fragmentationStrategy, properties);
		assertTrue(decoratedFragmentationStrategy instanceof SubstringFragmentationStrategy);
	}

}