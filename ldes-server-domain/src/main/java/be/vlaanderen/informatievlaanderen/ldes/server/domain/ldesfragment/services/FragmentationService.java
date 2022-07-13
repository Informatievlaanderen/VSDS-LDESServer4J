package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;

public interface FragmentationService {
    LdesMember addMember(LdesMember ldesMember);

    LdesFragment getFragment(String viewShortName, String path, String value);

    LdesFragment getInitialFragment(String collectionName, String timestampPath);
}
