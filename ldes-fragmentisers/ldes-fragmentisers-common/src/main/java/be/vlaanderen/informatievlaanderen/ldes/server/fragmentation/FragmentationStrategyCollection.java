package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.List;
import java.util.Map;

public interface FragmentationStrategyCollection {
	List<FragmentationStrategyExecutor> getFragmentationStrategyExecutors(String collectionName);

	List<ViewName> getViews(String collectionName);
}
