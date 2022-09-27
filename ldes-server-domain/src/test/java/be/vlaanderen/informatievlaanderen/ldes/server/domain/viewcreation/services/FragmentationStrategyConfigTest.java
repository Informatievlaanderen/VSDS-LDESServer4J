package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
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

	@Test
	void when_FragmentationStrategyConfigIsCorrectlyConfigured_MapOfFragmentationStrategiesIsReturned() {
		FragmentationStrategyCreator fragmentationStrategyCreator = mock(FragmentationStrategyCreator.class);
		ViewConfig viewConfig = getViewConfig();
		FragmentationStrategy firstCreatedFragmentationStrategy = mock(FragmentationStrategy.class);
		when(fragmentationStrategyCreator.createFragmentationStrategyForView(viewConfig.getViews().get(0)))
				.thenReturn(firstCreatedFragmentationStrategy);
		FragmentationStrategy secondCreatedFragmentationStrategy = mock(FragmentationStrategy.class);
		when(fragmentationStrategyCreator.createFragmentationStrategyForView(viewConfig.getViews().get(1)))
				.thenReturn(secondCreatedFragmentationStrategy);

		FragmentationStrategyConfig fragmentationStrategyConfig = new FragmentationStrategyConfig();
		Map<String, FragmentationStrategy> actualFragmentationStrategyMap = fragmentationStrategyConfig
				.fragmentationStrategyMap(fragmentationStrategyCreator, viewConfig);

		Map<String, FragmentationStrategy> expectedFragmentationStrategyMap = Map.of("firstView",
				firstCreatedFragmentationStrategy, "secondView", secondCreatedFragmentationStrategy);
		assertEquals(expectedFragmentationStrategyMap, actualFragmentationStrategyMap);
		InOrder inOrder = inOrder(fragmentationStrategyCreator);
		inOrder.verify(fragmentationStrategyCreator, times(1))
				.createFragmentationStrategyForView(viewConfig.getViews().get(0));
		inOrder.verify(fragmentationStrategyCreator, times(1))
				.createFragmentationStrategyForView(viewConfig.getViews().get(1));
		inOrder.verifyNoMoreInteractions();
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

	private FragmentationStrategyWrapper getFragmentationUpdater(ApplicationContext applicationContext,
			String timebased) {
		FragmentationStrategyWrapper timebasedFragmentationStrategyWrapper = mock(FragmentationStrategyWrapper.class);
		when(applicationContext.getBean(timebased)).thenReturn(timebasedFragmentationStrategyWrapper);
		return timebasedFragmentationStrategyWrapper;
	}

}