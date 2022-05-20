package be.vlaanderen.informatievlaanderen.ldes.server.domain.converters;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonConverterImplTest {

    private final JSONConverter jsonConverter = new JsonConverterImpl();

    @Test
    @DisplayName("Conversion of a proper json string")
    void when_StringIsProperJson_JSONObjectIsReturned() {
        String validJsonString = "{\n" + "  \"@context\": \"https://json-ld.org/contexts/person.jsonld\",\n"
                + "  \"@id\": \"http://dbpedia.org/resource/John_Lennon\",\n" + "  \"name\": \"John Lennon\",\n"
                + "  \"born\": \"1940-10-09\",\n" + "  \"spouse\": \"http://dbpedia.org/resource/Cynthia_Lennon\"\n"
                + "}";
        JSONObject jsonObject = jsonConverter.convertStringToJSONObject(validJsonString);
        assertEquals("https://json-ld.org/contexts/person.jsonld", jsonObject.get("@context"));
        assertEquals("http://dbpedia.org/resource/John_Lennon", jsonObject.get("@id"));
        assertEquals("John Lennon", jsonObject.get("name"));
        assertEquals("1940-10-09", jsonObject.get("born"));
        assertEquals("http://dbpedia.org/resource/Cynthia_Lennon", jsonObject.get("spouse"));
    }

    @Test
    @DisplayName("Conversion of an invalid json string")
    void when_StringIsInvalidJson_RuntimeExceptionIsThrown() {
        String validJsonString = "{\n" + "  \"@context\": \"https://json-ld.org/contexts/person.jsonld\"";
        assertThrows(RuntimeException.class, () -> jsonConverter.convertStringToJSONObject(validJsonString),
                "Following String could not be converted to a proper json object: " + validJsonString);
    }

}