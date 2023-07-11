package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;

public interface FragmentationStrategyCreator {
	FragmentationStrategy createFragmentationStrategyForView(ViewSpecification viewSpecification);
}
