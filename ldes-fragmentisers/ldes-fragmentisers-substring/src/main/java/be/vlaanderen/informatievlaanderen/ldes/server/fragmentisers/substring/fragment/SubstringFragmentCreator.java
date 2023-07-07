package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubstringFragmentCreator {

	public static final String SUBSTRING = "substring";

	private final FragmentRepository fragmentRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(SubstringFragmentCreator.class);

	public SubstringFragmentCreator(FragmentRepository fragmentRepository) {
		this.fragmentRepository = fragmentRepository;
	}

	public Fragment getOrCreateSubstringFragment(Fragment parentFragment, String substring) {
		Fragment child = parentFragment.createChild(new FragmentPair(SUBSTRING, substring));
		return fragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					fragmentRepository.saveFragment(child);
					LOGGER.debug("Substring fragment created with id: {}", child.getFragmentId());
					return child;
				});
	}
}
