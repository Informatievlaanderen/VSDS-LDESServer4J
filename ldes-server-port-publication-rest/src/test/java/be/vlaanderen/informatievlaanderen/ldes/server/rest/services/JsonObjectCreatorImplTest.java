package be.vlaanderen.informatievlaanderen.ldes.server.rest.services;

import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.LdesConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonObjectCreatorImplTest {

    private JsonObjectCreator jsonObjectCreator;

    @BeforeEach
    void init() {
        LdesConfig ldesConfig = new LdesConfig();
        ldesConfig.setId("some_id");
        ldesConfig.setContext("some_context");
        ldesConfig.setShape("some_shape");
        jsonObjectCreator = new JsonObjectCreatorImpl(ldesConfig);
    }

    @Test
    @DisplayName("Creation of a JSONObject")
    @SuppressWarnings("unchecked")
    void when_ContextIdAndItemsAreAvailable_JSONObjectIsCreated() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add("a");
        jsonArray.add("b");
        JSONObject jsonObject = jsonObjectCreator.createJSONObject(jsonArray);
        assertEquals(
                "{\"@id\":\"some_id\",\"@context\":\"some_context\",\"shape\":\"some_shape\",\"items\":[\"a\",\"b\"]}",
                jsonObject.toJSONString());

    }

}