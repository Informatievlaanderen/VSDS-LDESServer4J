package be.vlaanderen.informatievlaanderen.ldes.server.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import org.json.simple.JSONArray;

public interface LdesMemberConverter {
    JSONArray convertLdesMemberToJSONArray(LdesMember ldesMember);
}
