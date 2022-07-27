package be.vlaanderen.informatievlaanderen.ldes.server.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.LdesFragmentConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.entities.LdesFragmentView;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static org.apache.jena.riot.RDFFormat.*;

public class LdesFragmentHttpConverter implements HttpMessageConverter<LdesFragmentView> {

    private final LdesFragmentConverter ldesFragmentConverter;

    public LdesFragmentHttpConverter(LdesFragmentConverter ldesFragmentConverter) {
        this.ldesFragmentConverter = ldesFragmentConverter;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return clazz.isAssignableFrom(LdesFragmentView.class);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return List.of(new MediaType("application/n-quads"), new MediaType("application/ld+json"));
    }

    @Override
    public LdesFragmentView read(Class<? extends LdesFragmentView> clazz, HttpInputMessage inputMessage)
            throws HttpMessageNotReadableException {
        return null;
    }

    @Override
    public void write(LdesFragmentView ldesFragment, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        RDFFormat rdfFormat = getRdfFormat(contentType);
        OutputStream body = outputMessage.getBody();
        if(contentType.toString().equals("application/n-quads"))
            body.write(ldesFragment.getContent().getBytes());
        else {
            long start = System.currentTimeMillis();
            Model model = RdfModelConverter.fromString(ldesFragment.getContent(), Lang.NQUADS);
            long finish = System.currentTimeMillis();
            System.out.println("Processed in\t"+(finish - start)/1000.+"\tseconds.");
            start = System.currentTimeMillis();
            String s = RdfModelConverter.toString(model, rdfFormat);
            finish = System.currentTimeMillis();
            System.out.println("Processed in\t"+(finish - start)/1000.+"\tseconds.");
            body.write(s.getBytes());
        }
    }

    private RDFFormat getRdfFormat(MediaType contentType) {
        return switch (contentType.toString()) {
            case "application/n-quads" -> NQUADS;
            case "application/ld+json" -> JSONLD11;
            default -> JSONLD11;
        };
    }
}
