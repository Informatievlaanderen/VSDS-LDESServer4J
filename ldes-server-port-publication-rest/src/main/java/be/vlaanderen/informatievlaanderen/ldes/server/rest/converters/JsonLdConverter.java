package be.vlaanderen.informatievlaanderen.ldes.server.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.services.JsonObjectCreatorImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class JsonLdConverter implements HttpMessageConverter<LdesFragment> {

    private final LdesMemberConverter ldesMemberConverter = new LdesMemberConverterImpl();
    private final JsonObjectCreatorImpl jsonObjectCreator;

    public JsonLdConverter(JsonObjectCreatorImpl jsonObjectCreator) {
        this.jsonObjectCreator = jsonObjectCreator;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return clazz.isAssignableFrom(LdesFragment.class);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return List.of(MediaType.ALL);
    }

    @Override
    public LdesFragment read(Class<? extends LdesFragment> clazz, HttpInputMessage inputMessage)
            throws HttpMessageNotReadableException {
        return null;
    }

    @Override
    public void write(LdesFragment ldesFragment, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        JSONArray items = getItems(ldesFragment);
        JSONObject newJsonObject = jsonObjectCreator.createJSONObject(items);
        OutputStream body = outputMessage.getBody();
        body.write(newJsonObject.toJSONString().getBytes());
    }

    @SuppressWarnings("unchecked")
    private JSONArray getItems(LdesFragment ldesFragment) {
        JSONArray items = new JSONArray();
        ldesFragment.getRecords().stream().map(ldesMemberConverter::convertLdesMemberToJSONArray).forEach(items::add);
        return items;
    }
}
