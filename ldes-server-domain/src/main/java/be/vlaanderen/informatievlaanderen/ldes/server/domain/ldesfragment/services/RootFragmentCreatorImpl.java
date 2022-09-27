package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RootFragmentCreatorImpl implements RootFragmentCreator {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final LdesConfig ldesConfig;

	public RootFragmentCreatorImpl(LdesFragmentRepository ldesFragmentRepository, LdesConfig ldesConfig) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.ldesConfig = ldesConfig;
	}

	@Override
	public void createRootFragmentForView(String viewName) {
		Optional<LdesFragment> optionalRoot = ldesFragmentRepository
				.retrieveRootFragment(viewName);
		if (optionalRoot.isEmpty()) {
			createRoot(viewName);
		}
	}

	private void createRoot(String viewName) {
		FragmentInfo fragmentInfo = new FragmentInfo(
				viewName, List.of());
		LdesFragment ldesFragment = new LdesFragment(
				LdesFragmentNamingStrategy.generateFragmentName(ldesConfig.getHostName(), fragmentInfo.getViewName(),
						fragmentInfo.getFragmentPairs()),
				fragmentInfo);
		ldesFragmentRepository.saveFragment(ldesFragment);
	}
}
