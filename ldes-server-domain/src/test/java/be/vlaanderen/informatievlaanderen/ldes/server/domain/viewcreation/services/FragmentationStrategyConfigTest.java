package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.*;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

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
		LdesConfig ldesConfig = getLdesConfig();
		List<ViewSpecification> views = ldesConfig.getLdesStreams().get(0).getViews();
		FragmentationStrategy firstCreatedFragmentationStrategy = mock(FragmentationStrategy.class);
		when(fragmentationStrategyCreator.createFragmentationStrategyForView(views.get(0)))
				.thenReturn(firstCreatedFragmentationStrategy);
		FragmentationStrategy secondCreatedFragmentationStrategy = mock(FragmentationStrategy.class);
		when(fragmentationStrategyCreator.createFragmentationStrategyForView(views.get(1)))
				.thenReturn(secondCreatedFragmentationStrategy);

		FragmentationStrategyConfig fragmentationStrategyConfig = new FragmentationStrategyConfig();
		Map<String, FragmentationStrategy> actualFragmentationStrategyMap = fragmentationStrategyConfig
				.fragmentationStrategyMap(fragmentationStrategyCreator, ldesConfig);

		Map<String, FragmentationStrategy> expectedFragmentationStrategyMap = Map.of("parcels/firstView",
				firstCreatedFragmentationStrategy, "parcels/secondView", secondCreatedFragmentationStrategy);
		assertEquals(expectedFragmentationStrategyMap, actualFragmentationStrategyMap);
		InOrder inOrder = inOrder(fragmentationStrategyCreator);
		inOrder.verify(fragmentationStrategyCreator, times(1))
				.createFragmentationStrategyForView(views.get(0));
		inOrder.verify(fragmentationStrategyCreator, times(1))
				.createFragmentationStrategyForView(views.get(1));
		inOrder.verifyNoMoreInteractions();
	}

	private LdesConfig getLdesConfig() {
		LdesConfig ldesConfig = new LdesConfig();
		LdesSpecification ldesSpecification = getFirstLdesSpecification();
		ldesConfig.setLdesStreams(List.of(ldesSpecification));
		return ldesConfig;
	}

	private LdesSpecification getFirstLdesSpecification() {
		LdesSpecification ldesSpecification = new LdesSpecification();
		ldesSpecification.setHostName("http://localhost:8080");
		ldesSpecification.setCollectionName("parcels");
		ldesSpecification.setMemberType("https://vlaanderen.be/implementatiemodel/gebouwenregister#Perceel");
		ldesSpecification.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
		ldesSpecification.setViews(List.of(getFirstViewSpecification(), getSecondViewSpecification()));
		return ldesSpecification;
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

}
