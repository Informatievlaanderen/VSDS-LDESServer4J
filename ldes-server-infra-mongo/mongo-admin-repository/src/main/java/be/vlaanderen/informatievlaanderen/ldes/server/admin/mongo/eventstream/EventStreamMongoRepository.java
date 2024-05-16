package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.service.EventStreamConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;

import java.util.List;
import java.util.Optional;

public class EventStreamMongoRepository implements EventStreamRepository {
	private final EventStreamEntityRepository repository;
	private final EventStreamConverter converter;
	public EventStreamMongoRepository(EventStreamEntityRepository repository, EventStreamConverter converter) {
		this.repository = repository;
        this.converter = converter;
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
