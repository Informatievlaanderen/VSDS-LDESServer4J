package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.repository.EventSourceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.repository.EventSourceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.service.EventSourceEntityConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EventSourcePostgresRepository implements EventSourceRepository {

	private final EventSourceEntityRepository repository;
	private final EventSourceEntityConverter mapper;

	public EventSourcePostgresRepository(EventSourceEntityRepository repository, EventSourceEntityConverter mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public void saveEventSource(EventSource eventSource) {
		repository.save(mapper.toEventSourceEntity(eventSource));
	}

	@Override
	public Optional<EventSource> getEventSource(String collectionName) {
		return repository.findById(collectionName)
				.map(mapper::toEventSource);
	}

	@Override
	public List<EventSource> getAllEventSources() {
		return repository.findAll()
				.stream()
				.map(mapper::toEventSource)
				.toList();
	}
}
