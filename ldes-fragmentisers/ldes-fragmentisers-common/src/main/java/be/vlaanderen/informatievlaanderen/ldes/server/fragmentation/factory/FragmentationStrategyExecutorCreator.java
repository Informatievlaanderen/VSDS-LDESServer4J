package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyExecutor;

public interface FragmentationStrategyExecutorCreator {
	FragmentationStrategyExecutor createExecutor(ViewName viewName,
			ViewSpecification viewSpecification);
}
