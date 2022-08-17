package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import java.util.Optional;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;

public interface FragmentCreator {

	LdesFragment createNewFragment(Optional<LdesFragment> optionalExistingLdesFragment, FragmentPair bucket);

	boolean needsToCreateNewFragment(LdesFragment fragment);
}
