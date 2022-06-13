package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;

import java.util.Map;

public interface FragmentCreator {

    LdesFragment createFragment(Map<String, String> ldesFragmentConfig);
}
