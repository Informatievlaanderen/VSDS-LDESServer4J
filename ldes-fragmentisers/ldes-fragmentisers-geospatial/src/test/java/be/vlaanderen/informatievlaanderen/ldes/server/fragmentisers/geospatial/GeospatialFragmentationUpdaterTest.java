package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.FragmentationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeospatialFragmentationUpdaterTest {

	private final ApplicationContext applicationContext = mock(ApplicationContext.class);
	private final FragmentationService fragmentationService = mock(FragmentationService.class);
	private GeospatialFragmentationUpdater geospatialFragmentationUpdater;

	@BeforeEach
	void setUp() {
		geospatialFragmentationUpdater = new GeospatialFragmentationUpdater();
	}

	@Test
	void when_FragmentationServiceIsUpdated_GeospatialFragmentationServiceIsReturned() {
		FragmentationProperties properties = new FragmentationProperties(
				Map.of("maxZoomLevel", "15", "bucketiserProperty", "http://www.opengis.net/ont/geosparql#asWKT",
						"projection", "lambert72"));
		FragmentationService decoratedFragmentationService = geospatialFragmentationUpdater
				.updateFragmentationService(applicationContext, fragmentationService, properties);
		assertTrue(decoratedFragmentationService instanceof GeospatialFragmentationService);
	}
}