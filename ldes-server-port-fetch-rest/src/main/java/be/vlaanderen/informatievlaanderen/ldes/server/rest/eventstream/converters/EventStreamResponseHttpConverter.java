package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
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
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.FETCH;
import static be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.config.EventStreamWebConfig.DEFAULT_RDF_MEDIA_TYPE;

public class EventStreamResponseHttpConverter implements HttpMessageConverter<EventStreamResponse> {
	private final EventStreamResponseConverter eventStreamResponseConverter;
	private final RdfModelConverter rdfModelConverter;

	public EventStreamResponseHttpConverter(EventStreamResponseConverter eventStreamResponseConverter, RdfModelConverter rdfModelConverter) {
		this.eventStreamResponseConverter = eventStreamResponseConverter;
		this.rdfModelConverter = rdfModelConverter;
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
		return List.of(MediaType.valueOf(DEFAULT_RDF_MEDIA_TYPE), MediaType.ALL);
	}

	@Override
	public EventStreamResponse read(Class<? extends EventStreamResponse> clazz, HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(EventStreamResponse eventStreamResponse, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Lang rdfFormat = rdfModelConverter.getLang(contentType, FETCH);
		rdfModelConverter.checkLangForRelativeUrl(rdfFormat);
		Model eventStreamModel = eventStreamResponseConverter.toModel(eventStreamResponse);
		RDFDataMgr.write(outputMessage.getBody(), eventStreamModel, rdfFormat);
	}
}
