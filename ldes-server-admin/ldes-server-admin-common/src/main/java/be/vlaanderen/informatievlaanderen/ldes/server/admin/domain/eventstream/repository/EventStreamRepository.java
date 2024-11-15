package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;

import java.util.List;
import java.util.Optional;

public interface EventStreamRepository {
	List<EventStream> retrieveAllEventStreams();

	List<EventStreamTO> retrieveAllEventStreamTOs();

	Optional<EventStream> retrieveEventStream(String collectionName);

	Optional<EventStreamTO> retrieveEventStreamTO(String collectionName);

	Integer saveEventStream(EventStreamTO eventStreamTO);

	int deleteEventStream(String collectionName);

    void closeEventStream(String collectionName);
}
