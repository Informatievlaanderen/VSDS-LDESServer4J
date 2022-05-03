package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entity.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repository.LdesFragmentMongoRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LdesFragmentStorageService implements LdesFragmentRepository {

    private final LdesFragmentMongoRepository ldesFragmentMongoRepository;

    @Override
    public JSONObject saveLdesFragment(JSONObject ldesFragment) {
        LdesFragmentEntity savedLdesFragmentEntity = ldesFragmentMongoRepository.save(new LdesFragmentEntity(ldesFragment.hashCode(), ldesFragment));
        return savedLdesFragmentEntity.getLdesFragment();
    }
}
