package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import java.util.List;
import java.util.Optional;

public interface FragmentationStrategyCollection {
	List<FragmentationStrategyBatchExecutor> getFragmentationStrategyExecutors(String collectionName);
	Optional<FragmentationStrategyBatchExecutor> getFragmentationStrategyExecutor(String viewName);
}
