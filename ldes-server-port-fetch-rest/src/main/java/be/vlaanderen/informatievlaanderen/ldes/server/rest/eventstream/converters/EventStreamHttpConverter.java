package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.LdesProcessDirection.FETCH;

public class EventStreamHttpConverter implements HttpMessageConverter<EventStream> {

	private final EventStreamConverter eventStreamConverter;

	public EventStreamHttpConverter(EventStreamConverter eventStreamConverter) {
		this.eventStreamConverter = eventStreamConverter;
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return clazz.isAssignableFrom(EventStream.class);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.valueOf("text/turtle"), MediaType.valueOf("application/ld+json"),
				MediaType.valueOf("application/n-quads"));
	}

	@Override
	public EventStream read(Class<? extends EventStream> clazz, HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		return null;
	}

	@Override
	public void write(EventStream eventStream, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		OutputStream body = outputMessage.getBody();
		Lang rdfFormat = getLang(contentType, FETCH);
		Model fragmentModel = eventStreamConverter.toModel(eventStream);
		String outputString = RdfModelConverter.toString(fragmentModel, rdfFormat);
		body.write(outputString.getBytes());
	}

}
