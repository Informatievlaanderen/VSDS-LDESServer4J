package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class OpenFragmentProvider {

	private final TimeBasedFragmentCreator fragmentCreator;
	private final FragmentRepository fragmentRepository;
	private final Long memberLimit;

	public OpenFragmentProvider(TimeBasedFragmentCreator fragmentCreator,
			FragmentRepository fragmentRepository, Long memberLimit) {
		this.fragmentCreator = fragmentCreator;
		this.fragmentRepository = fragmentRepository;
		this.memberLimit = memberLimit;
	}

	public Pair<LdesFragment, Boolean> retrieveOpenFragmentOrCreateNewFragment(LdesFragment parentFragment) {
		return fragmentRepository
				.retrieveOpenChildFragment(parentFragment.getFragmentId())
				.map(fragment -> {
					if (needsToCreateNewFragment(fragment)) {
						LdesFragment newFragment = fragmentCreator.createNewFragment(fragment, parentFragment);
						fragmentRepository.saveFragment(newFragment);
						return new ImmutablePair<>(newFragment, false);
					} else {
						return new ImmutablePair<>(fragment, false);
					}
				})
				.orElseGet(() -> {
					LdesFragment newFragment = fragmentCreator.createNewFragment(parentFragment);
					fragmentRepository.saveFragment(newFragment);
					return new ImmutablePair<>(newFragment, true);
				});
	}

	public boolean needsToCreateNewFragment(LdesFragment fragment) {
		return fragment.getNumberOfMembers() >= memberLimit;
	}
}
