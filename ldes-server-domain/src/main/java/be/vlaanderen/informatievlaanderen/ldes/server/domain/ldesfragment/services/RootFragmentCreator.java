package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

public interface RootFragmentCreator {

	LdesFragment createRootFragmentForView(String viewName);
}
