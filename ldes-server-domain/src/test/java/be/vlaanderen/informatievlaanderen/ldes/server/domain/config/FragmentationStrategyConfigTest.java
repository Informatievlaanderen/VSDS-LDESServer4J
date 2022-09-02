package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationUpdater;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FragmentationStrategyConfigTest {

	private static final Map<String, String> TIMEBASED_PROPERTIES = Map.of("timebasedProperty", "time");
	private static final Map<String, String> GEOSPATIAL_PROPERTIES = Map.of("geospatialProperty", "geo");

	private static final Map<String, String> SECOND_TIMEBASED_PROPERTIES = Map.of("timebasedProperty", "secondtime");
	private static final String GEOSPATIAL = "geospatial";
	private static final String TIMEBASED = "timebased";
	private static final String COLLECTION_NAME = "collectionname";

	@Test
	void when_FragmentationStrategyConfigIsCorrectlyConfigured_DecoratedFragmentationServiceIsReturned() {
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		setUpLdesConfig(applicationContext);
		FragmentationUpdater timebasedFragmentationUpdater = getFragmentationUpdater(applicationContext, TIMEBASED);
		FragmentationService timebasedFragmentationService = mock(FragmentationService.class);
		FragmentationService secondTimebasedFragmentationService = mock(FragmentationService.class);
		when(timebasedFragmentationUpdater.updateFragmentationService(eq(applicationContext),
				any(),
				eq(new FragmentationProperties(TIMEBASED_PROPERTIES)))).thenReturn(timebasedFragmentationService);
		when(timebasedFragmentationUpdater.updateFragmentationService(eq(applicationContext),
				any(),
				eq(new FragmentationProperties(SECOND_TIMEBASED_PROPERTIES))))
				.thenReturn(secondTimebasedFragmentationService);

		FragmentationUpdater geospatialFragmentationUpdater = getFragmentationUpdater(applicationContext, GEOSPATIAL);
		FragmentationService geospatialFragmentationService = mock(FragmentationService.class);
		when(geospatialFragmentationUpdater.updateFragmentationService(applicationContext,
				timebasedFragmentationService, new FragmentationProperties(GEOSPATIAL_PROPERTIES)))
				.thenReturn(geospatialFragmentationService);

		ViewConfig viewConfig = getViewConfig();

		FragmentationStrategyConfig fragmentationStrategyConfig = new FragmentationStrategyConfig();
		Map<String, FragmentationService> fragmentationServiceMap = fragmentationStrategyConfig
				.fragmentationService(applicationContext, viewConfig);

		assertEquals(2, fragmentationServiceMap.size());
		assertEquals(geospatialFragmentationService, fragmentationServiceMap.get("firstView"));
		verify(timebasedFragmentationUpdater,
				times(1)).updateFragmentationService(eq(applicationContext), any(),
						eq(new FragmentationProperties(TIMEBASED_PROPERTIES)));
		verify(geospatialFragmentationUpdater,
				times(1)).updateFragmentationService(applicationContext,
						timebasedFragmentationService, new FragmentationProperties(GEOSPATIAL_PROPERTIES));
		assertEquals(secondTimebasedFragmentationService, fragmentationServiceMap.get("secondView"));
		verify(timebasedFragmentationUpdater,
				times(1)).updateFragmentationService(eq(applicationContext), any(),
						eq(new FragmentationProperties(SECOND_TIMEBASED_PROPERTIES)));
	}

	private ViewConfig getViewConfig() {
		ViewConfig viewConfig = new ViewConfig();
		ViewSpecification viewSpecification = getFirstViewSpecification();
		ViewSpecification secondViewSpecification = getSecondViewSpecification();
		viewConfig.setViews(List.of(viewSpecification, secondViewSpecification));
		return viewConfig;
	}

	private ViewSpecification getFirstViewSpecification() {
		ViewSpecification viewSpecification = new ViewSpecification();
		viewSpecification.setName("firstView");
		FragmentationConfig geospatialConfig = getFragmentationConfig(GEOSPATIAL, GEOSPATIAL_PROPERTIES);
		FragmentationConfig timebasedConfig = getFragmentationConfig(TIMEBASED, TIMEBASED_PROPERTIES);
		viewSpecification.setFragmentations(List.of(geospatialConfig, timebasedConfig));
		return viewSpecification;
	}

	private FragmentationConfig getFragmentationConfig(String name, Map<String, String> config) {
		FragmentationConfig geospatialConfig = new FragmentationConfig();
		geospatialConfig.setName(name);
		geospatialConfig.setConfig(config);
		return geospatialConfig;
	}

	private ViewSpecification getSecondViewSpecification() {
		ViewSpecification secondViewSpecification = new ViewSpecification();
		secondViewSpecification.setName("secondView");
		FragmentationConfig secondTimebasedConfig = getFragmentationConfig(TIMEBASED, SECOND_TIMEBASED_PROPERTIES);
		secondViewSpecification.setFragmentations(List.of(secondTimebasedConfig));
		return secondViewSpecification;
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

}