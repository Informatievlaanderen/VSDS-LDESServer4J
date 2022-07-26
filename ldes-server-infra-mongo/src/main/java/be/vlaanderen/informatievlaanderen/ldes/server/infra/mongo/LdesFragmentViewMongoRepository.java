package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.entities.LdesFragmentView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.repository.LdesFragmentViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentViewEntityRepository;

import java.util.Comparator;
import java.util.Optional;

public class LdesFragmentViewMongoRepository implements LdesFragmentViewRepository {

    private final LdesFragmentViewEntityRepository ldesFragmentViewEntityRepository;

    public LdesFragmentViewMongoRepository(LdesFragmentViewEntityRepository ldesFragmentViewEntityRepository) {
        this.ldesFragmentViewEntityRepository = ldesFragmentViewEntityRepository;
    }

    @Override
    public LdesFragmentView saveLdesFragmentView(LdesFragmentView ldesFragmentView) {
        ldesFragmentViewEntityRepository.save(LdesFragmentViewEntity.fromLdesFragmentView(ldesFragmentView));
        return ldesFragmentView;
    }

    @Override
    public Optional<LdesFragmentView> getFragmentViewById(String fragmentId) {
        return ldesFragmentViewEntityRepository
                .findById(fragmentId)
                .map(LdesFragmentViewEntity::toLdesFragmentView);
    }

    @Override
    public Optional<LdesFragmentView> getInitialFragment() {
        return ldesFragmentViewEntityRepository
                .findAll()
                .stream()
                .map(LdesFragmentViewEntity::toLdesFragmentView)
                .min(Comparator.comparing(LdesFragmentView::getFragmentId));
    }
}
