package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GeospatialFragmentationStrategyWrapperTest {

	private final ApplicationContext applicationContext = mock(ApplicationContext.class);
	private final FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	private GeospatialFragmentationStrategyWrapper geospatialFragmentationUpdater;

	@BeforeEach
	void setUp() {
		geospatialFragmentationUpdater = new GeospatialFragmentationStrategyWrapper();
	}

	@Test
	void when_FragmentationStrategyIsUpdated_GeospatialFragmentationStrategyIsReturned() {
		FragmentationProperties properties = new FragmentationProperties(
				Map.of("maxZoomLevel", "15", "bucketiserProperty", "http://www.opengis.net/ont/geosparql#asWKT",
						"projection", "lambert72"));
		FragmentationStrategy decoratedFragmentationStrategy = geospatialFragmentationUpdater
				.wrapFragmentationStrategy(applicationContext, fragmentationStrategy, properties);
		assertTrue(decoratedFragmentationStrategy instanceof GeospatialFragmentationStrategy);
	}
}