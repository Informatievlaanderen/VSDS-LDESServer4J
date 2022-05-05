package be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories;

import org.json.simple.JSONObject;

public interface LdesFragmentRepository {

    JSONObject saveLdesFragment(JSONObject ldesFragment);

    JSONObject retrieveLdesFragmentsPage(int page);
}
