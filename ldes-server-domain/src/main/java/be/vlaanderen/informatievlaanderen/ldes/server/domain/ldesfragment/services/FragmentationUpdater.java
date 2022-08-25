package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import org.springframework.context.ApplicationContext;

public interface FragmentationUpdater {
	FragmentationService updateFragmentationService(ApplicationContext applicationContext,
			FragmentationService fragmentationService);
}
