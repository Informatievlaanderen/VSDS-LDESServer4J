package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.collection;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.repository.EventStreamRepository;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class InMemoryEventStreamCollection implements EventStreamCollection {
	private final Map<String, EventStream> eventStreams = new HashMap<>();
	private final EventStreamRepository repository;

	public InMemoryEventStreamCollection(EventStreamRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<EventStream> retrieveAllEventStreams() {
		return List.copyOf(eventStreams.values());
	}

	@Override
	public Optional<EventStream> retrieveEventStream(String collectionName) {
		return Optional.ofNullable(eventStreams.get(collectionName));
	}

	@Override
	public EventStream saveEventStream(EventStream eventStream) {
		repository.saveEventStream(eventStream);
		eventStreams.put(eventStream.getCollection(), eventStream);
		return eventStream;
	}

	@Override
	public void deleteEventStream(String collectionName) {
		repository.deleteEventStream(collectionName);
		eventStreams.remove(collectionName);
	}

	@EventListener(ApplicationStartedEvent.class)
	public void initCollection() {
		Map<String, EventStream> initialized = repository.retrieveAllEventStreams()
				.stream()
				.collect(Collectors.toMap(EventStream::getCollection, Function.identity()));
		eventStreams.putAll(initialized);
	}
}
