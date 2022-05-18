package be.vlaanderen.informatievlaanderen.ldes.server.rest.services;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface JsonObjectCreator {
    JSONObject createJSONObject(final JSONArray items);
}
