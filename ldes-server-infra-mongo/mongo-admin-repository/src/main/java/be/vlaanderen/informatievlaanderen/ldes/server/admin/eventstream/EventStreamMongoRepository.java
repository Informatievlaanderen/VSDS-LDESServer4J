package be.vlaanderen.informatievlaanderen.ldes.server.admin.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.eventstream.service.EventStreamConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;

import java.util.List;
import java.util.Optional;

public class EventStreamMongoRepository implements EventStreamRepository {
	private final EventStreamEntityRepository repository;
	private final EventStreamConverter converter = new EventStreamConverter();

	public EventStreamMongoRepository(EventStreamEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<EventStream> retrieveAllEventStreams() {
		return repository.findAll()
				.stream()
				.map(converter::toEventStream)
				.toList();
	}

	@Override
	public Optional<EventStream> retrieveEventStream(String collectionName) {
		return repository.findById(collectionName).map(converter::toEventStream);
	}

	@Override
	public EventStream saveEventStream(EventStream eventStream) {
		repository.save(converter.fromEventStream(eventStream));
		return eventStream;
	}

	@Override
	public void deleteEventStream(String collectionName) {
		repository.deleteById(collectionName);
	}
}
