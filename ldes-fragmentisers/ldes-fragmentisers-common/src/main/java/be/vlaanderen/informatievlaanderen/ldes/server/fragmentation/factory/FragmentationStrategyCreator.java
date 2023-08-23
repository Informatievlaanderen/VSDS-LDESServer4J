package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;

public interface FragmentationStrategyCreator {
	FragmentationStrategy createFragmentationStrategyForView(ViewSpecification viewSpecification);
}
