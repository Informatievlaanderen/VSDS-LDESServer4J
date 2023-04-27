package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamHttpMessageConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamHttpMessage;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.REST_ADMIN;

public class EventStreamHttpConverter extends AbstractHttpMessageConverter<EventStreamHttpMessage> {
	private final EventStreamHttpMessageConverter eventStreamHttpMessageConverter;

	public EventStreamHttpConverter(EventStreamHttpMessageConverter eventStreamHttpMessageConverter) {
		this.eventStreamHttpMessageConverter = eventStreamHttpMessageConverter;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return EventStreamHttpMessage.class.isAssignableFrom(clazz);
	}

	@Override
	protected EventStreamHttpMessage readInternal(Class<? extends EventStreamHttpMessage> clazz,
			HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		Lang lang = getLang(Objects.requireNonNull(inputMessage.getHeaders().getContentType()), REST_ADMIN);
		Model eventStreamModel = RdfModelConverter
				.fromString(new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8), lang);
		return eventStreamHttpMessageConverter.fromModel(eventStreamModel);
	}

	@Override
	protected void writeInternal(EventStreamHttpMessage eventStreamHttpMessage, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Model eventStreamModel = eventStreamHttpMessageConverter.toModel(eventStreamHttpMessage);
		StringWriter outputStream = new StringWriter();

		RDFDataMgr.write(outputStream, eventStreamModel, Lang.TURTLE);

		OutputStream body = outputMessage.getBody();
		body.write(outputStream.toString().getBytes(StandardCharsets.UTF_8));
	}
}
