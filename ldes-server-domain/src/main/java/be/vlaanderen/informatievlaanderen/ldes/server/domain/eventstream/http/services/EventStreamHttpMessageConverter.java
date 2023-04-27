package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamHttpMessage;
import org.apache.jena.rdf.model.Model;

public interface EventStreamHttpMessageConverter {
	EventStreamHttpMessage fromModel(Model model);

	Model toModel(EventStreamHttpMessage eventStreamHttpMessage);
}
