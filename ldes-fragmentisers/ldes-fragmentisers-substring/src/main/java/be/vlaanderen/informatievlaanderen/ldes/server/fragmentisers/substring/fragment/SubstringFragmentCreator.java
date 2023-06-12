package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubstringFragmentCreator {

	public static final String SUBSTRING = "SubstringFragmentation";

	private final LdesFragmentRepository ldesFragmentRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(SubstringFragmentCreator.class);

	public SubstringFragmentCreator(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	public LdesFragment getOrCreateSubstringFragment(LdesFragment parentFragment, String substring) {
		LdesFragment child = parentFragment.createChild(new FragmentPair(SUBSTRING, substring));
		return ldesFragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					ldesFragmentRepository.saveFragment(child);
					LOGGER.debug("Substring fragment created with id: {}", child.getFragmentId());
					return child;
				});
	}
}
