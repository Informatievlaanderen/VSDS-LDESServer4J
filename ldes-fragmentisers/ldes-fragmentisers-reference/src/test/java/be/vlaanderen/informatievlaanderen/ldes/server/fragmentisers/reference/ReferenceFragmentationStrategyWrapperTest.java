package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.ReferenceFragmentationStrategyWrapper.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

class ReferenceFragmentationStrategyWrapperTest {

    private final ApplicationContext applicationContext = mock(ApplicationContext.class);
    private final FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
    private ReferenceFragmentationStrategyWrapper referenceFragmentationStrategyWrapper;

    @BeforeEach
    void setUp() {
        referenceFragmentationStrategyWrapper = new ReferenceFragmentationStrategyWrapper();
    }

    @Test
    void when_FragmentationStrategyIsUpdated_GeospatialFragmentationStrategyIsReturned() {
        ConfigProperties properties = new ConfigProperties(
                Map.of(FRAGMENTATION_PATH, DEFAULT_FRAGMENTATION_PATH, FRAGMENTATION_KEY, DEFAULT_FRAGMENTATION_KEY));
        FragmentationStrategy decoratedFragmentationStrategy = referenceFragmentationStrategyWrapper
                .wrapFragmentationStrategy(applicationContext, fragmentationStrategy, properties);
        assertInstanceOf(ReferenceFragmentationStrategy.class, decoratedFragmentationStrategy);
    }

}