package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

public class SubstringFragmentCreator {

	public static final String SUBSTRING = "substring";

	private final LdesFragmentRepository ldesFragmentRepository;

	public SubstringFragmentCreator(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	public LdesFragment getOrCreateSubstringFragment(LdesFragment parentFragment, String substring) {
		LdesFragment child = parentFragment.createChild(new FragmentPair(SUBSTRING, substring));
		return ldesFragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					ldesFragmentRepository.saveFragment(child);
					return child;
				});
	}
}
