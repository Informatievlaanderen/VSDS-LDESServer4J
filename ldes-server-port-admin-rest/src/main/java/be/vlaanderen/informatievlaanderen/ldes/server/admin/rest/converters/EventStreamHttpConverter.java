package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
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
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EventStreamHttpConverter implements HttpMessageConverter<EventStreamResponse> {
	private final EventStreamResponseConverter eventStreamResponseConverter = new EventStreamResponseConverter();

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return EventStreamResponse.class.isAssignableFrom(clazz);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.ALL);
	}

	@Override
	public EventStreamResponse read(Class<? extends EventStreamResponse> clazz, HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read an event stream response");
	}

	@Override
	public void write(EventStreamResponse eventStreamResponse, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Model eventStreamModel = eventStreamResponseConverter.toModel(eventStreamResponse);
		StringWriter outputStream = new StringWriter();

		RDFDataMgr.write(outputStream, eventStreamModel, Lang.TURTLE);

		OutputStream body = outputMessage.getBody();
		body.write(outputStream.toString().getBytes(StandardCharsets.UTF_8));
	}
}
