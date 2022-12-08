package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedFragmentationConfig;

public class OpenFragmentProvider {

	private final TimeBasedFragmentCreator fragmentCreator;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final TimebasedFragmentationConfig timebasedFragmentationConfig;

	public OpenFragmentProvider(TimeBasedFragmentCreator fragmentCreator,
			LdesFragmentRepository ldesFragmentRepository, TimebasedFragmentationConfig timebasedFragmentationConfig) {
		this.fragmentCreator = fragmentCreator;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.timebasedFragmentationConfig = timebasedFragmentationConfig;
	}

	public LdesFragment retrieveOpenFragmentOrCreateNewFragment(LdesFragment parentFragment) {
		return ldesFragmentRepository
				.retrieveOpenChildFragment(parentFragment.getFragmentInfo().generateFragmentId())
				.map(fragment -> {
					if (needsToCreateNewFragment(fragment)) {
						LdesFragment newFragment = fragmentCreator.createNewFragment(fragment, parentFragment);
						ldesFragmentRepository.saveFragment(newFragment);
						return newFragment;
					} else {
						return fragment;
					}
				})
				.orElseGet(() -> {
					LdesFragment newFragment = fragmentCreator.createNewFragment(parentFragment);
					ldesFragmentRepository.saveFragment(newFragment);
					return newFragment;
				});
	}

	public boolean needsToCreateNewFragment(LdesFragment fragment) {
		return fragment.getCurrentNumberOfMembers() >= timebasedFragmentationConfig.memberLimit();
	}
}
