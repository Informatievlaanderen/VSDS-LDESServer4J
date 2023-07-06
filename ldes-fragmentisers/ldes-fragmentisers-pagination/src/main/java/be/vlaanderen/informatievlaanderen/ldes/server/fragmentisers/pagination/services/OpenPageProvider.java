package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class OpenPageProvider {
	private final PageCreator pageCreator;
	private final FragmentRepository fragmentRepository;
	private final Long memberLimit;

	public OpenPageProvider(PageCreator pageCreator,
			FragmentRepository fragmentRepository, Long memberLimit) {
		this.pageCreator = pageCreator;
		this.fragmentRepository = fragmentRepository;
		this.memberLimit = memberLimit;
	}

	public ImmutablePair<LdesFragment, Boolean> retrieveOpenFragmentOrCreateNewFragment(LdesFragment parentFragment) {

		return fragmentRepository
				.retrieveOpenChildFragment(parentFragment.getFragmentId())
				.map(fragment -> {
					if (needsToCreateNewFragment(fragment)) {
						LdesFragment newFragment = pageCreator.createNewFragment(fragment, parentFragment);
						fragmentRepository.saveFragment(newFragment);
						return new ImmutablePair<>(newFragment, false);
					} else {
						return new ImmutablePair<>(fragment, false);
					}
				})
				.orElseGet(() -> {
					LdesFragment newFragment = pageCreator.createFirstFragment(parentFragment);
					fragmentRepository.saveFragment(newFragment);
					return new ImmutablePair<>(newFragment, true);
				});
	}

	public boolean needsToCreateNewFragment(LdesFragment fragment) {
		return fragment.getNumberOfMembers() >= memberLimit;
	}
}
