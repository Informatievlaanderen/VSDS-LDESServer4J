package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;

public interface FragmentFetchService {

	LdesFragment getFragment(LdesFragmentRequest ldesFragmentRequest);

	LdesFragment getInitialFragment(LdesFragmentRequest ldesFragmentRequest);
}
