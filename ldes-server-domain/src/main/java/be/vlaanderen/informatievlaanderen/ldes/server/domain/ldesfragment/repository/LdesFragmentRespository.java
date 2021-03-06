package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;

import java.util.Optional;

public interface LdesFragmentRespository {
    LdesFragment saveFragment(LdesFragment ldesFragment);

    Optional<LdesFragment> retrieveFragment(LdesFragmentRequest ldesFragmentRequest);

    Optional<LdesFragment> retrieveOpenFragment(String collectionName);

    Optional<LdesFragment> retrieveInitialFragment(String collectionName);
}
