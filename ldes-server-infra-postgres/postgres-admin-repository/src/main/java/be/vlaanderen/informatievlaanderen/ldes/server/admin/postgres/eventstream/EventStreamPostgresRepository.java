package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.mapper.EventStreamMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
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
        return repository.findAllPropertiesBy().stream()
                .map(EventStreamMapper::fromPropertiesProjection)
                .toList();
    }

    @Override
    public List<EventStreamTO> retrieveAllEventStreamTOs() {
        return repository.findAll().stream()
                .map(EventStreamMapper::fromEntity)
                .toList();
    }

    @Override
    public Optional<EventStream> retrieveEventStream(String collectionName) {
        return repository
                .findPropertiesByName(collectionName)
                .map(EventStreamMapper::fromPropertiesProjection);
    }

    @Override
    public Optional<EventStreamTO> retrieveEventStreamTO(String collectionName) {
        return repository.findByName(collectionName).map(EventStreamMapper::fromEntity);
    }

    @Override
    @Transactional
    public Integer saveEventStream(EventStreamTO eventStreamTO) {
        var es = repository.save(EventStreamMapper.toEntity(eventStreamTO));
        return es.getId();
    }

    @Override
    @Transactional
    public int deleteEventStream(String collectionName) {
        return repository.deleteByName(collectionName);
    }

    @Override
    @Transactional
    public void closeEventStream(String collectionName) {
        repository.closeEventStream(collectionName);
    }
}
