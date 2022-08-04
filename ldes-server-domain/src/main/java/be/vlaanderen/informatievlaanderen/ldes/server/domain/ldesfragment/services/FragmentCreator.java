package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import java.util.Optional;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;

public interface FragmentCreator {
	
    LdesFragment createNewFragment(Optional<LdesFragment> optionalExistingLdesFragment, LdesMember firstMember);
    boolean needsToCreateNewFragment(LdesFragment fragment);
}
