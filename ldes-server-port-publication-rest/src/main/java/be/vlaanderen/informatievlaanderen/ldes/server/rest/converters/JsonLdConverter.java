package be.vlaanderen.informatievlaanderen.ldes.server.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.services.JsonObjectCreatorImpl;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;

import static org.apache.jena.riot.RDFFormat.NQUADS;

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
        Model fragmentModel = ldesFragment.toRdfOutputModel();

        StringWriter outputStream = new StringWriter();

        RDFDataMgr.write(outputStream, fragmentModel, NQUADS);

        OutputStream body = outputMessage.getBody();
        body.write(outputStream.toString().getBytes());
    }
}
