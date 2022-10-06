package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;


public class OpenFragmentProvider {

	private final TimeBasedFragmentCreator fragmentCreator;
	private final LdesFragmentRepository ldesFragmentRepository;

	public OpenFragmentProvider(TimeBasedFragmentCreator fragmentCreator,
			LdesFragmentRepository ldesFragmentRepository) {
		this.fragmentCreator = fragmentCreator;
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	public LdesFragment retrieveOpenFragmentOrCreateNewFragment(LdesFragment parentFragment) {
		return ldesFragmentRepository
				.retrieveOpenChildFragment(parentFragment.getFragmentInfo().getViewName(),
						parentFragment.getFragmentInfo().getFragmentPairs())
				.map(fragment -> {
					if (fragmentCreator.needsToCreateNewFragment(fragment)) {
						return fragmentCreator.createNewFragment(fragment, parentFragment);
					} else {
						return fragment;
					}
				})
				.orElseGet(() -> fragmentCreator.createNewFragment(parentFragment));
	}
}
