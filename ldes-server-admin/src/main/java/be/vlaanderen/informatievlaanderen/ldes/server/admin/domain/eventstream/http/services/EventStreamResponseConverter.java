package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.http.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.http.valueobjects.EventStreamResponse;
import org.apache.jena.rdf.model.Model;

public interface EventStreamResponseConverter {
	EventStreamResponse fromModel(Model model);

	Model toModel(EventStreamResponse eventStreamResponse);
}
