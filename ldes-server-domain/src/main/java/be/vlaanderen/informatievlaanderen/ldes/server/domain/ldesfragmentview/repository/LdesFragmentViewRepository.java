package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.entities.LdesFragmentView;

import java.util.Optional;

public interface LdesFragmentViewRepository {

    LdesFragmentView saveLdesFragmentView(LdesFragmentView ldesFragmentView);
    Optional<LdesFragmentView> getFragmentViewById(String fragmentId);

    Optional<LdesFragmentView> getInitialFragment();


}
