package be.vlaanderen.informatievlaanderen.ldes.server.domain.converters;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonConverterImpl implements JSONConverter {
    private final JSONParser parser = new JSONParser();

    public JSONObject convertStringToJSONObject(String jsonLDString) {
        try {
            return (JSONObject) parser.parse(jsonLDString);
        } catch (ParseException e) {
            throw new RuntimeException(
                    String.format("Following String could not be converted to a proper json object: %s", jsonLDString),
                    e);
        }
    }
}
