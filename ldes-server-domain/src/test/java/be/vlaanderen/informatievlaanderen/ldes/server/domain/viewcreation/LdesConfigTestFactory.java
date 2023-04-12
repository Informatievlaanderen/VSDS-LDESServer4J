package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.*;

import java.util.List;
import java.util.Map;

public class LdesConfigTestFactory {

	private static final Map<String, String> TIMEBASED_PROPERTIES = Map.of("timebasedProperty", "time");
	private static final Map<String, String> GEOSPATIAL_PROPERTIES = Map.of("geospatialProperty", "geo");

	private static final Map<String, String> SECOND_TIMEBASED_PROPERTIES = Map.of("timebasedProperty", "secondtime");
	private static final String GEOSPATIAL = "geospatial";
	private static final String TIMEBASED = "timebased";

	public static LdesConfig getLdesConfig() {
		LdesConfig ldesConfig = new LdesConfig();
		LdesSpecification ldesSpecification = getFirstLdesSpecification();
		ldesConfig.setLdesStreams(List.of(ldesSpecification));
		return ldesConfig;
	}

	private static LdesSpecification getFirstLdesSpecification() {
		LdesSpecification ldesSpecification = new LdesSpecification();
		ldesSpecification.setHostName("http://localhost:8080");
		ldesSpecification.setCollectionName("parcels");
		ldesSpecification.setMemberType("https://vlaanderen.be/implementatiemodel/gebouwenregister#Perceel");
		ldesSpecification.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
		ldesSpecification.setViews(List.of(getFirstViewSpecification(), getSecondViewSpecification()));
		return ldesSpecification;
	}

	private static ViewSpecification getFirstViewSpecification() {
		ViewSpecification viewSpecification = new ViewSpecification();
		viewSpecification.setName("firstView");
		FragmentationConfig geospatialConfig = getFragmentationConfig(GEOSPATIAL, GEOSPATIAL_PROPERTIES);
		FragmentationConfig timebasedConfig = getFragmentationConfig(TIMEBASED, TIMEBASED_PROPERTIES);
		viewSpecification.setFragmentations(List.of(geospatialConfig, timebasedConfig));
		return viewSpecification;
	}

	private static FragmentationConfig getFragmentationConfig(String name, Map<String, String> config) {
		FragmentationConfig geospatialConfig = new FragmentationConfig();
		geospatialConfig.setName(name);
		geospatialConfig.setConfig(config);
		return geospatialConfig;
	}

	private static ViewSpecification getSecondViewSpecification() {
		ViewSpecification secondViewSpecification = new ViewSpecification();
		secondViewSpecification.setName("secondView");
		FragmentationConfig secondTimebasedConfig = getFragmentationConfig(TIMEBASED, SECOND_TIMEBASED_PROPERTIES);
		secondViewSpecification.setFragmentations(List.of(secondTimebasedConfig));
		return secondViewSpecification;
	}

}
