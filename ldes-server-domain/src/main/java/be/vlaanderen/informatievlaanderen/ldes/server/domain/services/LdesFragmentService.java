package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import org.json.simple.JSONObject;

public interface LdesFragmentService {
    JSONObject storeLdesFragment(final JSONObject ldesFragment);

    JSONObject retrieveLdesFragmentsPage(final int page);
}
