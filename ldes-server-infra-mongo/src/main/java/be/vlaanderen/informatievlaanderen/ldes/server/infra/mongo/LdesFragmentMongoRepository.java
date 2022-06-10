package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentEntityRepository;

import java.util.Optional;

public class LdesFragmentMongoRepository implements LdesFragmentRespository {
    private final LdesFragmentEntityRepository repository;

    public LdesFragmentMongoRepository(LdesFragmentEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public LdesFragment saveFragment(LdesFragment ldesFragment) {
        repository.save(LdesFragmentEntity.fromLdesFragment(ldesFragment));
        return ldesFragment;
    }

    @Override
    public Optional<LdesFragment> retrieveFragment(String viewShortName, String path, String value) {
        return repository.findClosestFragments(viewShortName, path, value).findFirst()
                .map(LdesFragmentEntity::toLdesFragment);
    }

    @Override
    public Optional<LdesFragment> retrieveLastFragment(String view) {
        return repository.findAll().stream()
                .filter(ldesFragmentEntity -> !ldesFragmentEntity.isImmutable())
                .map(LdesFragmentEntity::toLdesFragment)
                .findFirst();
    }
}
