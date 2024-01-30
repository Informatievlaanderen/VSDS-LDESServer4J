package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import io.micrometer.observation.annotation.Observed;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.common.reflect.TypeToken;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.REST_ADMIN;

@Observed
@Component
public class EventStreamListHttpConverter implements GenericHttpMessageConverter<List<EventStreamResponse>> {
	private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.valueOf("text/turtle");
	private final EventStreamResponseConverter eventStreamResponseConverter;
	private final RdfModelConverter rdfModelConverter;

	public EventStreamListHttpConverter(EventStreamResponseConverter eventStreamResponseConverter, RdfModelConverter rdfModelConverter) {
		this.eventStreamResponseConverter = eventStreamResponseConverter;
		this.rdfModelConverter = rdfModelConverter;
	}

	@Override
	public boolean canRead(@NotNull Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canRead(@NotNull Type type, Class<?> contextClass, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(@NotNull Class<?> clazz, MediaType mediaType) {
		return List.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean canWrite(Type type, @NotNull Class<?> clazz, MediaType mediaType) {
		TypeToken<List<EventStreamResponse>> expectedType = new TypeToken<>() {
		};
		return canWrite(clazz, mediaType) && expectedType.isSupertypeOf(type);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(DEFAULT_MEDIA_TYPE, MediaType.ALL);
	}

	@Override
	public List<EventStreamResponse> read(@NotNull Class<? extends List<EventStreamResponse>> clazz,
			@NotNull HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a list of event stream responses");
	}

	@Override
	public List<EventStreamResponse> read(@NotNull Type type, Class<?> contextClass, @NotNull HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a list of event stream responses");
	}

	@Override
	public void write(List<EventStreamResponse> eventStreamResponses, MediaType contentType,
			HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		Model model = ModelFactory.createDefaultModel();
		eventStreamResponses.stream()
				.map(eventStreamResponseConverter::toModel)
				.forEach(model::add);
		Lang lang = rdfModelConverter.getLang(contentType, REST_ADMIN);
		rdfModelConverter.checkLangForRelativeUrl(lang);
		outputMessage.getHeaders().setContentType(MediaType.valueOf(lang.getHeaderString()));
		RDFDataMgr.write(outputMessage.getBody(), model, lang);
	}

	@Override
	public void write(@NotNull List<EventStreamResponse> eventStreamResponses, Type type, MediaType contentType,
					  @NotNull HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		write(eventStreamResponses, contentType, outputMessage);
	}
}
