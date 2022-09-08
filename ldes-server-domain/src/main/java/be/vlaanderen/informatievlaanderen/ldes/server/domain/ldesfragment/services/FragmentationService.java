package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

public interface FragmentationService {
	void addMemberToFragment(LdesFragment parentFragment, String ldesMemberId);
}