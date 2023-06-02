package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.Map;

public interface FragmentationStrategyCollection {
	Map<ViewName, FragmentationStrategy> getFragmentationStrategyMap();
}
