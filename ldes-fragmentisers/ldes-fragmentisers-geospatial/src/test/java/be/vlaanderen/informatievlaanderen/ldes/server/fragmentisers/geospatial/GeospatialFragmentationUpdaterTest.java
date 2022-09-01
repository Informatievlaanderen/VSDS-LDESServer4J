package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

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
		GeospatialConfig geospatialConfig = new GeospatialConfig();
		geospatialConfig.setProjection("lambert72");
		when(applicationContext.getBean(GeospatialConfig.class)).thenReturn(geospatialConfig);
		FragmentationService fragmentationService1 = geospatialFragmentationUpdater
				.updateFragmentationService(applicationContext, fragmentationService);
		assertTrue(fragmentationService1 instanceof GeospatialFragmentationService);
	}
}