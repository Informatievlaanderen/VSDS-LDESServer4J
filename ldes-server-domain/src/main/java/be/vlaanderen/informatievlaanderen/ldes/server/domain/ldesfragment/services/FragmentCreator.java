package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;

import java.util.Optional;

public interface FragmentCreator {
    LdesFragment createNewFragment(Optional<LdesFragment> optionalExistingLdesFragment, LdesMember firstMember);
}
