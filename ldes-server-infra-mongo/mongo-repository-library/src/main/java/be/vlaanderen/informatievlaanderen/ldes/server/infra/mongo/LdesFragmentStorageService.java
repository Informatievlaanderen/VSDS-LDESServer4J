package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entity.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repository.LdesFragmentMongoRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class LdesFragmentStorageService implements LdesFragmentRepository {

    private final LdesFragmentMongoRepository ldesFragmentMongoRepository;
    private final LdesFragmentCreator ldesFragmentCreator;

    @Override
    public JSONObject saveLdesFragment(JSONObject ldesFragment) {
        LdesFragmentEntity savedLdesFragmentEntity = ldesFragmentMongoRepository
                .save(new LdesFragmentEntity(ldesFragment.hashCode(), ldesFragment));
        return savedLdesFragmentEntity.getLdesFragment();
    }

    public JSONObject retrieveLdesFragmentsPage(int page) {
        Pageable paging = PageRequest.of(page, 1);
        Page<LdesFragmentEntity> pageable = ldesFragmentMongoRepository.findAll(paging);
        return ldesFragmentCreator.createLdesFragmentPage(pageable);
    }
}
