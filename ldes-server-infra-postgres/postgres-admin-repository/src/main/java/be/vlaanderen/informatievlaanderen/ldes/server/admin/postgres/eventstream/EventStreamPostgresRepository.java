package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.service.EventStreamConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.mapper.EventStreamMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import com.google.common.collect.Streams;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class EventStreamPostgresRepository implements EventStreamRepository {
	private final be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository oldModelRepo;
	private final EventStreamEntityRepository repository;
	private final EventStreamConverter converter = new EventStreamConverter();

	public EventStreamPostgresRepository(be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository oldModelRepo, @Qualifier("collectionsRepository") EventStreamEntityRepository repository) {
		this.oldModelRepo = oldModelRepo;
        this.repository = repository;
	}

	@Override
	public List<EventStream> retrieveAllEventStreams() {
		final var oldStreams = oldModelRepo.findAll()
				.stream()
				.map(converter::toEventStream);
		final var newStreams = repository.findAll().stream()
				.map(EventStreamMapper::fromEntity);
		return Streams.concat(oldStreams, newStreams).toList();
	}

	@Override
	public Optional<EventStream> retrieveEventStream(String collectionName) {
		return repository
				.findByName(collectionName)
				.map(EventStreamMapper::fromEntity)
				.or(() -> oldModelRepo.findById(collectionName).map(converter::toEventStream));
	}

	@Override
	@Transactional
	public EventStream saveEventStream(EventStream eventStream) {
		oldModelRepo.save(converter.fromEventStream(eventStream));
		repository.save(EventStreamMapper.toEntity(eventStream));
		return eventStream;
	}

	@Override
	@Transactional
	public void deleteEventStream(String collectionName) {
		repository.deleteByName(collectionName);
		oldModelRepo.deleteById(collectionName);
	}
}
