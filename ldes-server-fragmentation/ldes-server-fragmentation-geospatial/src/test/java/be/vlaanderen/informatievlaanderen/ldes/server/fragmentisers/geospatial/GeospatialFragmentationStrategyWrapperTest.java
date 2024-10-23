package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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
		ConfigProperties properties = new ConfigProperties(
				Map.of("maxZoom", "15", "fragmentationPath", "http://www.opengis.net/ont/geosparql#asWKT"));

		FragmentationStrategy decoratedFragmentationStrategy = geospatialFragmentationUpdater
				.wrapFragmentationStrategy(applicationContext, fragmentationStrategy, properties);

		assertThat(decoratedFragmentationStrategy).isInstanceOf(GeospatialFragmentationStrategy.class);
	}
}
