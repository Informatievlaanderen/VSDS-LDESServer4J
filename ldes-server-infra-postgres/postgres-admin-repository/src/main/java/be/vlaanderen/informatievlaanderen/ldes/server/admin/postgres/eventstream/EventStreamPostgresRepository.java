package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.mapper.EventStreamMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class EventStreamPostgresRepository implements EventStreamRepository {
    private final EventStreamEntityRepository repository;

    public EventStreamPostgresRepository(EventStreamEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<EventStream> retrieveAllEventStreams() {
        return repository.findAll().stream()
                .map(EventStreamMapper::fromEntity)
                .toList();
    }

    @Override
    public Optional<EventStream> retrieveEventStream(String collectionName) {
        return repository
                .findByName(collectionName)
                .map(EventStreamMapper::fromEntity);
    }

    @Override
    @Transactional
    public EventStream saveEventStream(EventStream eventStream) {
        repository.save(EventStreamMapper.toEntity(eventStream));
        return eventStream;
    }

    @Override
    @Transactional
    public void deleteEventStream(String collectionName) {
        repository.deleteByName(collectionName);
    }
}
