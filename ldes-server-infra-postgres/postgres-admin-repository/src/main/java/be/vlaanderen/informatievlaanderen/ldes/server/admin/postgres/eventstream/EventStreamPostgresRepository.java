package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.service.EventStreamConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class EventStreamPostgresRepository implements EventStreamRepository {
	private final EventStreamEntityRepository repository;
	private final EventStreamConverter converter = new EventStreamConverter();

	public EventStreamPostgresRepository(EventStreamEntityRepository repository) {
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
	@Transactional
	public EventStream saveEventStream(EventStream eventStream) {
		repository.save(converter.fromEventStream(eventStream));
		return eventStream;
	}

	@Override
	@Transactional
	public void deleteEventStream(String collectionName) {
		repository.deleteById(collectionName);
	}
}