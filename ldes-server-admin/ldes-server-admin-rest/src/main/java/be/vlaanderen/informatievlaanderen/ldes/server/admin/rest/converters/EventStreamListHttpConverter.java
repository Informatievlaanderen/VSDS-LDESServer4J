package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamWriter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfMediaType;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import com.google.common.reflect.TypeToken;
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

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.REST_ADMIN;

@Observed
@Component
public class EventStreamListHttpConverter implements GenericHttpMessageConverter<List<EventStreamTO>> {
	private final EventStreamWriter eventStreamWriter;
	private final RdfModelConverter rdfModelConverter;

	public EventStreamListHttpConverter(EventStreamWriter eventStreamWriter, RdfModelConverter rdfModelConverter) {
		this.eventStreamWriter = eventStreamWriter;
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
		TypeToken<List<EventStreamTO>> expectedType = new TypeToken<>() {
		};
		return canWrite(clazz, mediaType) && expectedType.isSupertypeOf(type);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
        return RdfMediaType.getMediaTypes();
	}

	@Override
	public List<EventStreamTO> read(@NotNull Class<? extends List<EventStreamTO>> clazz,
                                    @NotNull HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a list of event stream responses");
	}

	@Override
	public List<EventStreamTO> read(@NotNull Type type, Class<?> contextClass, @NotNull HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a list of event stream responses");
	}

	@Override
	public void write(List<EventStreamTO> eventStreamRespons, MediaType contentType,
                      HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		Model model = ModelFactory.createDefaultModel();
		eventStreamRespons.stream()
				.map(eventStreamWriter::write)
				.forEach(model::add);
		Lang lang = rdfModelConverter.getLangOrDefault(contentType, REST_ADMIN);
		rdfModelConverter.checkLangForRelativeUrl(lang);
		outputMessage.getHeaders().setContentType(MediaType.parseMediaType(lang.getHeaderString()));
		RDFDataMgr.write(outputMessage.getBody(), model, lang);
	}

	@Override
	public void write(@NotNull List<EventStreamTO> eventStreamRespons, Type type, MediaType contentType,
                      @NotNull HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		write(eventStreamRespons, contentType, outputMessage);
	}
}
