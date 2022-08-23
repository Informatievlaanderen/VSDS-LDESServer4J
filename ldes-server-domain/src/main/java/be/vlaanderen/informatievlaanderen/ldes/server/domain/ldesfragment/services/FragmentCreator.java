package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import java.util.List;
import java.util.Optional;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;

public interface FragmentCreator {

	LdesFragment createNewFragment(Optional<LdesFragment> optionalExistingLdesFragment,
			List<FragmentPair> fragmentPairsOfParent);

	boolean needsToCreateNewFragment(LdesFragment fragment);
}
