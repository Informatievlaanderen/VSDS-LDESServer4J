package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RootFragmentCreatorImpl implements RootFragmentCreator {

	private final LdesFragmentRepository ldesFragmentRepository;

	public RootFragmentCreatorImpl(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override
	public LdesFragment createRootFragmentForView(ViewName viewName) {
		return ldesFragmentRepository
				.retrieveRootFragment(viewName.toString())
				.orElseGet(() -> createRoot(viewName));
	}

	private LdesFragment createRoot(ViewName viewName) {
		LdesFragment ldesFragment = new LdesFragment(viewName, List.of());
		return ldesFragmentRepository.saveFragment(ldesFragment);
	}
}
