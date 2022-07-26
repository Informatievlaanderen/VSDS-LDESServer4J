package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.entities.LdesFragmentView;

public interface FragmentViewingService {

    LdesFragmentView getFragment(LdesFragmentRequest ldesFragmentRequest);

    LdesFragmentView getInitialFragment(String collectionName);

    void saveImmutableLdesFragment(LdesFragment ldesFragment);
}
