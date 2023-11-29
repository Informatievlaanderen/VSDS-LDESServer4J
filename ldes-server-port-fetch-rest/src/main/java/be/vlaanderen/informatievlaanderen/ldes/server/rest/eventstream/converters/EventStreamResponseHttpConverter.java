package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.RequestContextExtracter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.USE_RELATIVE_URL_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.FETCH;

public class EventStreamResponseHttpConverter implements HttpMessageConverter<EventStreamResponse> {
	private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.valueOf("text/turtle");
	private final EventStreamResponseConverter eventStreamResponseConverter;
	private final RequestContextExtracter requestContextExtracter;
	private final boolean useRelativeUrl;

	public EventStreamResponseHttpConverter(EventStreamResponseConverter eventStreamResponseConverter,
											RequestContextExtracter requestContextExtracter,
											Boolean useRelativeUrl) {
		this.eventStreamResponseConverter = eventStreamResponseConverter;
		this.requestContextExtracter = requestContextExtracter;
		this.useRelativeUrl = useRelativeUrl;
	}

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
		return List.of(DEFAULT_MEDIA_TYPE, MediaType.ALL);
	}

	@Override
	public EventStreamResponse read(Class<? extends EventStreamResponse> clazz, HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(EventStreamResponse eventStreamResponse, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Lang rdfFormat = getLang(contentType, FETCH);
		Model eventStreamModel = eventStreamResponseConverter.toModel(eventStreamResponse);
		if(useRelativeUrl) {
			eventStreamModel.write(outputMessage.getBody(), rdfFormat.getName(), requestContextExtracter.extractRequestURL());
		} else {
			eventStreamModel.write(outputMessage.getBody(), rdfFormat.getName());
		}
	}
}
