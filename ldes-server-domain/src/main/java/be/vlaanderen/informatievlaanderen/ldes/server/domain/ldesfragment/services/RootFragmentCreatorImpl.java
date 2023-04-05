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
	public void createRootFragmentForView(String viewName) {
		Optional<LdesFragment> optionalRoot = ldesFragmentRepository
				.retrieveRootFragment(viewName);
		if (optionalRoot.isEmpty()) {
			createRoot(viewName);
		}
	}

	private void createRoot(String viewName) {
		LdesFragment ldesFragment = new LdesFragment(viewName, List.of());
		ldesFragmentRepository.saveFragment(ldesFragment);
	}
}
