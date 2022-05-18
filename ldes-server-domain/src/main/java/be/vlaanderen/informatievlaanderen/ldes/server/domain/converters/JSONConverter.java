package be.vlaanderen.informatievlaanderen.ldes.server.domain.converters;

import org.json.simple.JSONObject;

public interface JSONConverter {

    JSONObject convertStringToJSONObject(String jsonLDString);
}
