package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStreamResponse;
import org.apache.jena.rdf.model.Model;

public interface EventStreamConverter {

	Model toModel(final EventStreamResponse eventStreamResponse);
}
