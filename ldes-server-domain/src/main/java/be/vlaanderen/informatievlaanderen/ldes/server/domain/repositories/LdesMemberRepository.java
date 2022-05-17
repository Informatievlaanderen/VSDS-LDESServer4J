package be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import org.json.simple.JSONObject;

public interface LdesMemberRepository {

    LdesMember saveLdesMember(LdesMember ldesMember);

    /**
     * @deprecated Since we switched from JSONObjects to a more general LdesMember
     */
    @Deprecated(forRemoval = true)
    JSONObject retrieveLdesFragmentsPage(int page);

    /**
     * @deprecated Since we switched from JSONObjects to a more general LdesMember
     */
    @Deprecated(forRemoval = true)
    JSONObject saveLdesMember(JSONObject ldesFragment);
}
