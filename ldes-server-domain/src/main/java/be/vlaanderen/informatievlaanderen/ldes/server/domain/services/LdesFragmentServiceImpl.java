package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesFragmentRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LdesFragmentServiceImpl implements LdesFragmentService {

    private final LdesFragmentRepository ldesFragmentRepository;
    
    @Autowired
    public LdesFragmentServiceImpl(final LdesFragmentRepository ldesFragmentRepository) {
    	this.ldesFragmentRepository = ldesFragmentRepository;
    }

    public JSONObject storeLdesFragment(final JSONObject ldesFragment) {
        return ldesFragmentRepository.saveLdesFragment(ldesFragment);
    }

    public JSONObject retrieveLdesFragmentsPage(final int page) {
        return ldesFragmentRepository.retrieveLdesFragmentsPage(page);
    }
}
