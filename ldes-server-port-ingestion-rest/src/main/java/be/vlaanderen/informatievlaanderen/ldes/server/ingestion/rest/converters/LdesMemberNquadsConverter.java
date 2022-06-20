package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
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

import static org.apache.jena.riot.RDFFormat.NQUADS;

public class LdesMemberNquadsConverter extends AbstractHttpMessageConverter<LdesMember> {

    public LdesMemberNquadsConverter() {
        super(new MediaType("application", "n-quads"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(LdesMember.class);
    }

    @Override
    protected LdesMember readInternal(Class<? extends LdesMember> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        // TODO get MIME type and use this to parse. (support different formats)
        Model memberModel = RDFParserBuilder.create()
                .fromString(new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8)).lang(Lang.NQUADS)
                .toModel();
        return new LdesMember(memberModel);
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
