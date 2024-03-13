package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTOConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import io.micrometer.observation.annotation.Observed;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.REST_ADMIN;

@Observed
@Component
public class EventStreamHttpConverter implements HttpMessageConverter<EventStreamTO> {
	private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.valueOf("text/turtle");
	private final EventStreamTOConverter eventStreamTOConverter;
	private final RdfModelConverter rdfModelConverter;

	public EventStreamHttpConverter(EventStreamTOConverter eventStreamTOConverter, RdfModelConverter rdfModelConverter) {
		this.eventStreamTOConverter = eventStreamTOConverter;
		this.rdfModelConverter = rdfModelConverter;
	}

	@Override
	public boolean canRead(@NotNull Class<?> clazz, @Nullable MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(@NotNull Class<?> clazz, MediaType mediaType) {
		return EventStreamTO.class.isAssignableFrom(clazz);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(DEFAULT_MEDIA_TYPE, MediaType.ALL);
	}

	@Override
	public EventStreamTO read(@NotNull Class<? extends EventStreamTO> clazz, @NotNull HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read an event stream response");
	}

	@Override
	public void write(@NotNull EventStreamTO eventStreamTO, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Model eventStreamModel = eventStreamTOConverter.toModel(eventStreamTO);
		Lang lang = rdfModelConverter.getLang(contentType, REST_ADMIN);
		rdfModelConverter.checkLangForRelativeUrl(lang);
		outputMessage.getHeaders().setContentType(contentType);
		RDFDataMgr.write(outputMessage.getBody(), eventStreamModel, lang);
	}
}
