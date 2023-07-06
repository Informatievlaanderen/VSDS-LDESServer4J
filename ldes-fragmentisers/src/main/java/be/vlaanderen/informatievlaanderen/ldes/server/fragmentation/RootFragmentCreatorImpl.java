package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RootFragmentCreatorImpl implements RootFragmentCreator {

	private final FragmentRepository fragmentRepository;

	public RootFragmentCreatorImpl(FragmentRepository fragmentRepository) {
		this.fragmentRepository = fragmentRepository;
	}

	@Override
	public LdesFragment createRootFragmentForView(ViewName viewName) {
		return fragmentRepository
				.retrieveRootFragment(viewName.asString())
				.orElseGet(() -> createRoot(viewName));
	}

	private LdesFragment createRoot(ViewName viewName) {
		LdesFragment ldesFragment = new LdesFragment(new LdesFragmentIdentifier(viewName, List.of()));
		return fragmentRepository.saveFragment(ldesFragment);
	}
}
