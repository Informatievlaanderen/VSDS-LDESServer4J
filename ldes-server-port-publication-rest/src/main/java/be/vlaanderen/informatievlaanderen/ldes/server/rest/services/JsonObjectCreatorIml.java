package be.vlaanderen.informatievlaanderen.ldes.server.rest.services;

import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.LdesConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class JsonObjectCreatorIml implements JsonObjectCreator {
    private final LdesConfig ldesConfig;

    public JsonObjectCreatorIml(final LdesConfig ldesConfig) {
        this.ldesConfig = ldesConfig;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject createJSONObject(final JSONArray items) {
        JSONObject newJsonObject = new JSONObject();
        newJsonObject.put("@context", ldesConfig.getContext());
        newJsonObject.put("@id", ldesConfig.getId());
        newJsonObject.put("items", items);
        return newJsonObject;
    }
}
