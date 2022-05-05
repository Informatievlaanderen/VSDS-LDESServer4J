package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesFragmentRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LdesFragmentServiceImpl implements LdesFragmentService {

    private final LdesFragmentRepository ldesFragmentRepository;

    public JSONObject storeLdesFragment(final JSONObject ldesFragment) {
        return ldesFragmentRepository.saveLdesFragment(ldesFragment);
    }

    public JSONObject retrieveLdesFragmentsPage(final int page) {
        return ldesFragmentRepository.retrieveLdesFragmentsPage(page);
    }
}
