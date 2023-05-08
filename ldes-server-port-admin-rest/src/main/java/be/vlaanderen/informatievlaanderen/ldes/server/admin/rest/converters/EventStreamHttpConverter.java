package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.springframework.http.MediaType;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.REST_ADMIN;

public class EventStreamHttpConverter {
	private static final EventStreamResponseConverter eventStreamResponseConverter = new EventStreamResponseConverter();

	public static String toString(EventStreamResponse eventStreamResponse, String contentType) {
		Lang lang = getLang(MediaType.valueOf(contentType), REST_ADMIN);
		Model eventStreamModel = eventStreamResponseConverter.toModel(eventStreamResponse);

		return RDFWriter.source(eventStreamModel)
				.lang(lang)
				.asString();
	}

	public static String toString(List<EventStreamResponse> eventStreamResponses, String contentType) {
		Lang lang = getLang(MediaType.valueOf(contentType), REST_ADMIN);
		Model model = ModelFactory.createDefaultModel();
		eventStreamResponses.stream()
				.map(eventStreamResponseConverter::toModel)
				.forEach(model::add);

		return RDFWriter.source(model)
				.lang(lang)
				.asString();
	}
}
