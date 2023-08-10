package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.valueobjects.TreeNodeDto;

public interface TreeNodeFetcher {
	TreeNodeDto getFragment(LdesFragmentRequest ldesFragmentRequest);
}
