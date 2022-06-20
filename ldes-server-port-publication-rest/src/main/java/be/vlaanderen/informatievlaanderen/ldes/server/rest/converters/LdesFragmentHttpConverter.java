package be.vlaanderen.informatievlaanderen.ldes.server.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
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

public class LdesFragmentHttpConverter implements HttpMessageConverter<LdesFragment> {

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
        return List.of(new MediaType("application/ld+json"), new MediaType("application/nquads"));
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
        RDFFormat rdfFormat = getRdfFormat(contentType);
        Model fragmentModel = ldesFragment.toRdfOutputModel();
        StringWriter outputStream = new StringWriter();
        RDFDataMgr.write(outputStream, fragmentModel, rdfFormat);
        body.write(outputStream.toString().getBytes());
    }

    private RDFFormat getRdfFormat(MediaType contentType) {
        return switch (contentType.toString()) {
        case "application/n-quads" -> NQUADS;
        case "application/ld+json" -> JSONLD11;
        default -> throw new UnsupportedOperationException(
                String.format("No converter implemented for MediaType %s", contentType));
        };
    }
}
