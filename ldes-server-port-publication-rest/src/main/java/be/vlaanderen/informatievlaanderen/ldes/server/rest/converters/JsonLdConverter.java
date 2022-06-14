package be.vlaanderen.informatievlaanderen.ldes.server.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
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

import static org.apache.jena.riot.RDFFormat.JSONLD11;
import static org.apache.jena.riot.RDFFormat.NQUADS;

public class JsonLdConverter implements HttpMessageConverter<LdesFragment> {

    public JsonLdConverter() {

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

        OutputStream body = outputMessage.getBody();

        final RDFFormat rdfFormat;
        if ("application/n-quads".equals(contentType.toString())) {
            rdfFormat = NQUADS;
        } else {
            rdfFormat = JSONLD11;
        }

        Model fragmentModel = ldesFragment.toRdfOutputModel();

        StringWriter outputStream = new StringWriter();

        RDFDataMgr.write(outputStream, fragmentModel, rdfFormat);

        body.write(outputStream.toString().getBytes());
    }
}
