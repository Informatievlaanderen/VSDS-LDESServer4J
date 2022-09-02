package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationUpdater;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FragmentationStrategyConfigTest {

	private static final Map<String, String> TIMEBASED_PROPERTIES = Map.of("timebasedProperty", "time");
	private static final Map<String, String> GEOSPATIAL_PROPERTIES = Map.of("geospatialProperty", "geo");
	private static final String GEOSPATIAL = "geospatial";
	private static final String TIMEBASED = "timebased";
	private static final String COLLECTION_NAME = "collectionname";

	@Test
	void when_FragmentationStrategyConfigIsCorrectlyConfigured_DecoratedFragmentationServiceIsReturned() {
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		setUpLdesConfig(applicationContext);
		FragmentationUpdater timebasedFragmentationUpdater = getFragmentationUpdater(applicationContext, TIMEBASED);
		FragmentationService timebasedFragmentationService = mock(FragmentationService.class);
		when(timebasedFragmentationUpdater.updateFragmentationService(eq(applicationContext), any(),
				eq(new FragmentationProperties(TIMEBASED_PROPERTIES)))).thenReturn(timebasedFragmentationService);
		FragmentationUpdater geospatialFragmentationUpdater = getFragmentationUpdater(applicationContext, GEOSPATIAL);
		FragmentationService geospatialFragmentationService = mock(FragmentationService.class);
		when(geospatialFragmentationUpdater.updateFragmentationService(applicationContext,
				timebasedFragmentationService, new FragmentationProperties(GEOSPATIAL_PROPERTIES)))
				.thenReturn(geospatialFragmentationService);

		FragmentationConfig fragmentationConfig = getFragmentationConfig();

		FragmentationStrategyConfig fragmentationStrategyConfig = new FragmentationStrategyConfig();
		FragmentationService decoratedFragmentationService = fragmentationStrategyConfig
				.fragmentationService(applicationContext, fragmentationConfig);

		assertEquals(geospatialFragmentationService, decoratedFragmentationService);
		verify(timebasedFragmentationUpdater, times(1)).updateFragmentationService(eq(applicationContext), any(),
				eq(new FragmentationProperties(TIMEBASED_PROPERTIES)));
		verify(geospatialFragmentationUpdater, times(1)).updateFragmentationService(applicationContext,
				timebasedFragmentationService, new FragmentationProperties(GEOSPATIAL_PROPERTIES));
	}

	private FragmentationUpdater getFragmentationUpdater(ApplicationContext applicationContext, String timebased) {
		FragmentationUpdater timebasedFragmentationUpdater = mock(FragmentationUpdater.class);
		when(applicationContext.getBean(timebased)).thenReturn(timebasedFragmentationUpdater);
		return timebasedFragmentationUpdater;
	}

	private void setUpLdesConfig(ApplicationContext applicationContext) {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setCollectionName(COLLECTION_NAME);
		when(applicationContext.getBean(LdesConfig.class)).thenReturn(ldesConfig);
		when(applicationContext.getBean(LdesMemberRepository.class)).thenReturn(mock(LdesMemberRepository.class));
		when(applicationContext.getBean(LdesFragmentRepository.class)).thenReturn(mock(LdesFragmentRepository.class));
	}

	private FragmentationConfig getFragmentationConfig() {
		FragmentationConfig timebasedConfig = new FragmentationConfig();
		timebasedConfig.setName(TIMEBASED);
		timebasedConfig.setConfig(TIMEBASED_PROPERTIES);

		FragmentationConfig geospatialConfig = new FragmentationConfig();
		geospatialConfig.setName(GEOSPATIAL);
		geospatialConfig.setConfig(GEOSPATIAL_PROPERTIES);
		geospatialConfig.setFragmentation(timebasedConfig);

		return geospatialConfig;
	}

}