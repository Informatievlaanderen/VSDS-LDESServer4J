package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.FragmentationProperties;
import org.springframework.context.ApplicationContext;

public interface FragmentationUpdater {
	FragmentationService updateFragmentationService(ApplicationContext applicationContext,
			FragmentationService fragmentationService, FragmentationProperties properties);
}
