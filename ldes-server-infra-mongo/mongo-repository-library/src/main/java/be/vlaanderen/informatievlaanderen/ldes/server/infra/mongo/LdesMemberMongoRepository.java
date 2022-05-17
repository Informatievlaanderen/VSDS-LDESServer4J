package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.services.LdesFragmentCreator;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class LdesMemberMongoRepository implements LdesMemberRepository {

    private final LdesFragmentRepository ldesFragmentRepository;
    private final LdesFragmentCreator ldesFragmentCreator;

    @Override
    public JSONObject saveLdesMember(JSONObject ldesFragment) {
        LdesFragmentEntity savedLdesFragmentEntity = ldesFragmentRepository
                .save(new LdesFragmentEntity(ldesFragment.hashCode(), ldesFragment));
        return savedLdesFragmentEntity.getLdesFragment();
    }

    @Override
    public LdesMember saveLdesMember(LdesMember ldesMember) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public JSONObject retrieveLdesFragmentsPage(int page) {
        Pageable paging = PageRequest.of(page, 1);
        Page<LdesFragmentEntity> pageable = ldesFragmentRepository.findAll(paging);
        return ldesFragmentCreator.createLdesFragmentPage(pageable);
    }
}
