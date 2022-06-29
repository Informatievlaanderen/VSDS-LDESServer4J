package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;

import java.util.Optional;

public interface FragmentCreator {
    LdesFragment createNewFragment(Optional<LdesFragment> optionalExistingLdesFragment, LdesMember firstMember);
}
