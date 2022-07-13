package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

public interface FragmentationService {

    LdesFragment getFragment(String viewShortName, String path, String value);

    LdesFragment getInitialFragment(String collectionName, String timestampPath);
}
