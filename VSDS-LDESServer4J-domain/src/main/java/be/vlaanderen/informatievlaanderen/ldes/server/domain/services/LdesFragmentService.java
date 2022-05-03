package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LdesFragmentService {

    private final LdesFragmentRepository ldesFragmentRepository;

    public JSONObject storeLdesFragment(final JSONObject ldesFragment){
        return ldesFragmentRepository.saveLdesFragment(ldesFragment);
    }
}
