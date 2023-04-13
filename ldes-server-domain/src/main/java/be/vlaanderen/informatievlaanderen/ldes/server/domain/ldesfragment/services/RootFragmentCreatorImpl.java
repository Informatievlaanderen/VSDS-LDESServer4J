package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RootFragmentCreatorImpl implements RootFragmentCreator {

	private final LdesFragmentRepository ldesFragmentRepository;

	public RootFragmentCreatorImpl(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override
	public LdesFragment createRootFragmentForView(String viewName) {
		Optional<LdesFragment> optionalRoot = ldesFragmentRepository
				.retrieveRootFragment(viewName);
		if (optionalRoot.isEmpty()) {
			return createRoot(viewName);
		}
		return optionalRoot.get();
	}

	private LdesFragment createRoot(String viewName) {
		LdesFragment ldesFragment = new LdesFragment("collectionName", viewName, List.of());
		return ldesFragmentRepository.saveFragment(ldesFragment);
	}
}
