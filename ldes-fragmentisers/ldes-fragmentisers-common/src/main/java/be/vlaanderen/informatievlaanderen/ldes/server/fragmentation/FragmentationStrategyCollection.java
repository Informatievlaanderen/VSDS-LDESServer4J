package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import java.util.List;

public interface FragmentationStrategyCollection {
	List<FragmentationStrategyExecutor> getFragmentationStrategyExecutors(String collectionName);
}
