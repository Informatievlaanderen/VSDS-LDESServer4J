package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamServiceSpi;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public interface EventStreamService extends EventStreamServiceSpi {

	void deleteEventStream(String collectionName);

	EventStreamTO createEventStream(EventStreamTO eventStream);

	void updateEventSource(String collectionName, List<Model> eventSourceModel);

	void closeEventStream(String collectionName);
}
