package be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;

import java.util.Optional;

public interface LdesFragmentRespository {
    LdesFragment saveFragment(LdesFragment ldesFragment);

    Optional<LdesFragment> retrieveFragment(String viewShortName, String path, String value);

    Optional<LdesFragment> retrieveLastFragment(String view);
}
