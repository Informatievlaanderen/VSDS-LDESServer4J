package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamWriter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfMediaType;
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

public class EventStreamResponseHttpConverter implements HttpMessageConverter<EventStreamTO> {
	private final EventStreamWriter eventStreamWriter;
	private final RdfModelConverter rdfModelConverter;
	public EventStreamResponseHttpConverter(EventStreamWriter eventStreamWriter, RdfModelConverter rdfModelConverter) {
		this.eventStreamWriter = eventStreamWriter;
		this.rdfModelConverter = rdfModelConverter;
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return EventStreamTO.class.isAssignableFrom(clazz);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
        return RdfMediaType.getMediaTypes();
	}

	@Override
	public EventStreamTO read(Class<? extends EventStreamTO> clazz, HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(EventStreamTO eventStreamTO, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Lang rdfFormat = rdfModelConverter.getLangOrDefault(contentType, FETCH);
		rdfModelConverter.checkLangForRelativeUrl(rdfFormat);
		Model eventStreamModel = eventStreamWriter.write(eventStreamTO);
		RDFDataMgr.write(outputMessage.getBody(), eventStreamModel, rdfFormat);
	}
}
