package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.List;
import java.util.Map;

public interface FragmentationStrategyCollection {
	Map<ViewName, FragmentationStrategy> getFragmentationStrategyMap();

	List<FragmentationStrategyExecutor> getFragmentationStrategyExecutors();

	List<ViewName> getViews(String collectionName);
}
