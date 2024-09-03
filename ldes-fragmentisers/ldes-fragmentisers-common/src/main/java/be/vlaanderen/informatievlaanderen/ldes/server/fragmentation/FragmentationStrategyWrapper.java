package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ConfigProperties;
import org.springframework.context.ApplicationContext;

public interface FragmentationStrategyWrapper {
	FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
	                                                FragmentationStrategy fragmentationStrategy,
	                                                ConfigProperties fragmentationProperties);
}
