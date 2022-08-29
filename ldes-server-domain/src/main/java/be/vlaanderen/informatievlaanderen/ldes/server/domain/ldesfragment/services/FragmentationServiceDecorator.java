package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

public abstract class FragmentationServiceDecorator implements FragmentationService {

	private final FragmentationService fragmentationService;

	protected FragmentationServiceDecorator(FragmentationService fragmentationService) {
		this.fragmentationService = fragmentationService;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, String ldesMemberId) {
		fragmentationService.addMemberToFragment(parentFragment, ldesMemberId);
	}

}
