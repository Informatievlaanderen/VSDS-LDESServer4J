package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.RequestContextExtracter;
import org.apache.jena.ext.com.google.common.reflect.TypeToken;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.REST_ADMIN;

public class EventStreamListHttpConverter implements GenericHttpMessageConverter<List<EventStreamResponse>> {
	private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.valueOf("text/turtle");
	private final EventStreamResponseConverter eventStreamResponseConverter;
	private final RequestContextExtracter requestContextExtracter;
	private final boolean useRelativeUrl;

	public EventStreamListHttpConverter(EventStreamResponseConverter eventStreamResponseConverter, RequestContextExtracter requestContextExtracter, Boolean useRelativeUrl) {
		this.eventStreamResponseConverter = eventStreamResponseConverter;
		this.requestContextExtracter = requestContextExtracter;
		this.useRelativeUrl = useRelativeUrl;
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return List.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
		TypeToken<List<EventStreamResponse>> expectedType = new TypeToken<>() {
		};
		return canWrite(clazz, mediaType) && expectedType.isSupertypeOf(type);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(DEFAULT_MEDIA_TYPE, MediaType.ALL);
	}

	@Override
	public List<EventStreamResponse> read(Class<? extends List<EventStreamResponse>> clazz,
			HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a list of event stream responses");
	}

	@Override
	public List<EventStreamResponse> read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a list of event stream responses");
	}

	@Override
	public void write(List<EventStreamResponse> eventStreamResponses, MediaType contentType,
			HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		Model model = ModelFactory.createDefaultModel();
		Lang rdfFormat = getLang(contentType, REST_ADMIN);
		eventStreamResponses.stream()
				.map(eventStreamResponseConverter::toModel)
				.forEach(model::add);

		if(useRelativeUrl) {
			model.write(outputMessage.getBody(), rdfFormat.getName(), requestContextExtracter.extractRequestURL());
		} else {
			model.write(outputMessage.getBody(), rdfFormat.getName());
		}
	}

	@Override
	public void write(List<EventStreamResponse> eventStreamResponses, Type type, MediaType contentType,
			HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		write(eventStreamResponses, contentType, outputMessage);
	}
}
