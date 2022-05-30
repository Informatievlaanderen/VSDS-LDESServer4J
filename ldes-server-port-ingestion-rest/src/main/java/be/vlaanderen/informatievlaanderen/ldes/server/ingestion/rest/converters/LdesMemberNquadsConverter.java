package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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
        return new LdesMember(new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8).split("\n"));
    }

    @Override
    protected void writeInternal(LdesMember ldesMember, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        OutputStream body = outputMessage.getBody();
        body.write(String.join("\n", ldesMember.getQuads()).getBytes());
    }

}
