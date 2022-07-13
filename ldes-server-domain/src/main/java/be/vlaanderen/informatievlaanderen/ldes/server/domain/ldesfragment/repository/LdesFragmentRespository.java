package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

import java.util.Optional;

public interface LdesFragmentRespository {
    LdesFragment saveFragment(LdesFragment ldesFragment);

    Optional<LdesFragment> retrieveFragment(String viewShortName, String path, String value);

    Optional<LdesFragment> retrieveOpenFragment(String collectionName);

    Optional<LdesFragment> retrieveInitialFragment(String collectionName);
}
