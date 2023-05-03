package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EventStreamListHttpConverter implements HttpMessageConverter<List<EventStreamResponse>> {
	private final EventStreamResponseConverter eventStreamResponseConverter = new EventStreamResponseConverter();

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return List.class.isAssignableFrom(clazz);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.ALL);
	}

	@Override
	public List<EventStreamResponse> read(Class<? extends List<EventStreamResponse>> clazz,
			HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a list of event stream responses");
	}

	@Override
	public void write(List<EventStreamResponse> eventStreamResponses, MediaType contentType,
			HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		Model model = ModelFactory.createDefaultModel();
		eventStreamResponses.stream()
				.map(eventStreamResponseConverter::toModel)
				.forEach(model::add);

		StringWriter outputStream = new StringWriter();

		RDFDataMgr.write(outputStream, model, Lang.TURTLE);

		outputMessage.getBody().write(outputStream.toString().getBytes(StandardCharsets.UTF_8));
	}
}
