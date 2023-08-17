package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
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
	public Fragment createRootFragmentForView(ViewName viewName) {
		return fragmentRepository
				.retrieveRootFragment(viewName.asString())
				.orElseGet(() -> createRoot(viewName));
	}

	private Fragment createRoot(ViewName viewName) {
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(viewName, List.of()));
		return fragmentRepository.saveFragment(fragment);
	}
}
