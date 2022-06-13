package be.vlaanderen.informatievlaanderen.ldes.server.rest.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonObjectCreatorImplTest {


    @BeforeEach
    void init() {
        LdesFragmentConfig ldesFragmentConfig = new LdesFragmentConfig();
        ldesFragmentConfig.setView("some_id");
        ldesFragmentConfig.setContext("some_context");
        ldesFragmentConfig.setShape("some_shape");
    }

    @Test
    @DisplayName("Creation of a JSONObject")
    @SuppressWarnings("unchecked")
    void when_ContextIdAndItemsAreAvailable_JSONObjectIsCreated() {
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add("a");
//        jsonArray.add("b");
//        JSONObject jsonObject = jsonObjectCreator.createJSONObject(jsonArray);
//        assertEquals(
//                "{\"shape\":\"some_shape\",\"@id\":\"some_id\",\"@context\":\"some_context\",\"items\":[\"a\",\"b\"]}",
//                jsonObject.toJSONString());

    }

}