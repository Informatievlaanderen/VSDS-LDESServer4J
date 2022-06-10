package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;

public interface FragmentationService {
    LdesMember addMember(LdesMember ldesMember);

    LdesFragment getFragment(String viewShortName, String path, String value);
}
