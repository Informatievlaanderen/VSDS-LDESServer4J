package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import java.util.Optional;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;

public interface FragmentCreator {

	LdesFragment createNewFragment(Optional<LdesFragment> optionalExistingLdesFragment,
			FragmentInfo parentFragmentInfo);

	boolean needsToCreateNewFragment(LdesFragment fragment);
}
