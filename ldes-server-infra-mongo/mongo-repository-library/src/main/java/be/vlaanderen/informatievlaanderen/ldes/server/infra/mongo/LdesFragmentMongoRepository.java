package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.services.LdesFragmentCreator;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class LdesFragmentMongoRepository
        implements be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesFragmentRepository {

    private final LdesFragmentRepository ldesFragmentRepository;
    private final LdesFragmentCreator ldesFragmentCreator;
    
    public LdesFragmentMongoRepository(final LdesFragmentRepository ldesFragmentRepository, final LdesFragmentCreator ldesFragmentCreator) {
    	this.ldesFragmentRepository = ldesFragmentRepository;
    	this.ldesFragmentCreator = ldesFragmentCreator;
    }

    @Override
    public JSONObject saveLdesFragment(JSONObject ldesFragment) {
        LdesFragmentEntity savedLdesFragmentEntity = ldesFragmentRepository
                .save(new LdesFragmentEntity(ldesFragment.hashCode(), ldesFragment));
        return savedLdesFragmentEntity.getLdesFragment();
    }

    @Override
    public JSONObject retrieveLdesFragmentsPage(int page) {
        Pageable paging = PageRequest.of(page, 1);
        Page<LdesFragmentEntity> pageable = ldesFragmentRepository.findAll(paging);
        return ldesFragmentCreator.createLdesFragmentPage(pageable);
    }
}
