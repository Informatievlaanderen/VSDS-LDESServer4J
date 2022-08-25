package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

import java.util.List;

public interface FragmentationService {

	List<LdesFragment> addMemberToFragment(List<LdesFragment> parentFragments, String ldesMemberId);
}
