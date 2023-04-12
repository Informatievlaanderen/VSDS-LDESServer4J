package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RootFragmentCreatorImpl implements RootFragmentCreator {

	private final LdesFragmentRepository ldesFragmentRepository;

	public RootFragmentCreatorImpl(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override
	public LdesFragment createRootFragmentForView(String viewName) {
		return ldesFragmentRepository
				.retrieveRootFragment(viewName)
				.orElseGet(() -> createRoot(viewName));
	}

	private LdesFragment createRoot(String viewName) {
		LdesFragment ldesFragment = new LdesFragment(viewName.split("/")[0], viewName, List.of());
		return ldesFragmentRepository.saveFragment(ldesFragment);
	}
}
