package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;

import java.util.Optional;

public class OpenFragmentProvider {

	private final TimeBasedFragmentCreator fragmentCreator;
	private final LdesFragmentRepository ldesFragmentRepository;

	public OpenFragmentProvider(TimeBasedFragmentCreator fragmentCreator,
			LdesFragmentRepository ldesFragmentRepository) {
		this.fragmentCreator = fragmentCreator;
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	public LdesFragment retrieveOpenFragmentOrCreateNewFragment(FragmentInfo fragmentInfo) {
		return ldesFragmentRepository
				.retrieveOpenChildFragment(fragmentInfo.getViewName(),
						fragmentInfo.getFragmentPairs())
				.map(fragment -> {
					if (fragmentCreator.needsToCreateNewFragment(fragment)) {
						return fragmentCreator.createNewFragment(Optional.of(fragment), fragmentInfo);
					} else {
						return fragment;
					}
				})
				.orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(), fragmentInfo));
	}
}
