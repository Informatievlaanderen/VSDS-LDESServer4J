package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;

import java.util.List;

public interface FragmentationService {
	void addMemberToFragment(List<FragmentPair> fragmentPairList, String ldesMemberId);
}
