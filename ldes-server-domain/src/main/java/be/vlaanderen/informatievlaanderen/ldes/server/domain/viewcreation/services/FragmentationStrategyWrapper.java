package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import org.springframework.context.ApplicationContext;

public interface FragmentationStrategyWrapper {
	FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties);
}
