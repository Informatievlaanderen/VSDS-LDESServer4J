package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;

public interface FragmentationStrategyCreator {
	FragmentationStrategy createFragmentationStrategyForView(ViewSpecification viewSpecification);
}
