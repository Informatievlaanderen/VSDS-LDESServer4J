package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.apache.jena.riot.RDFFormat.*;

public class LdesMemberConverter extends AbstractHttpMessageConverter<LdesMember> {

    public LdesMemberConverter() {
        super(new MediaType("application", "n-quads"), new MediaType("application", "n-triples"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(LdesMember.class);
    }

    @Override
    protected LdesMember readInternal(Class<? extends LdesMember> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        Lang lang = getLang(Objects.requireNonNull(inputMessage.getHeaders().getContentType()));
        Model memberModel = RDFParserBuilder.create()
                .fromString(new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8)).lang(lang)
                .toModel();
        return new LdesMember(memberModel);
    }

    private Lang getLang(MediaType contentType) {
        return switch ("%s/%s".formatted(contentType.getType(), contentType.getSubtype())) {
            case "application/n-quads" -> Lang.NQUADS;
            case "application/n-triples" -> Lang.NTRIPLES;
            default -> Lang.NQUADS;
        };
    }

    @Override
    protected void writeInternal(LdesMember ldesMember, HttpOutputMessage outputMessage)
            throws UnsupportedOperationException, HttpMessageNotWritableException, IOException {
        Model fragmentModel = ldesMember.getModel();

        StringWriter outputStream = new StringWriter();

        RDFDataMgr.write(outputStream, fragmentModel, NQUADS);

        OutputStream body = outputMessage.getBody();
        body.write(outputStream.toString().getBytes());
    }

}
