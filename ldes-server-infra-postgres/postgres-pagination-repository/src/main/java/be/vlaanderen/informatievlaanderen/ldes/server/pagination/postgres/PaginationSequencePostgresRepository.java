package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper.PaginationSequenceEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PaginationSequenceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PaginationSequenceRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Primary
public class PaginationSequencePostgresRepository implements PaginationSequenceRepository {

    private static final String COLLECTION_VIEW_SEPARATOR = "/";

    private final PaginationSequenceEntityRepository repository;
    private final PaginationSequenceEntityMapper mapper = new PaginationSequenceEntityMapper();

    public PaginationSequencePostgresRepository(PaginationSequenceEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<FragmentSequence> findLastProcessedSequence(ViewName viewName) {
        return repository
                .findById(viewName.asString())
                .map(mapper::toFragmentSequence);
    }

    @Override
    public void saveLastProcessedSequence(FragmentSequence sequence) {
        repository.save(mapper.toEntity(sequence));
    }

    @Override
    @Transactional
    public void deleteByViewName(ViewName viewName) {
        repository.deleteById(viewName.asString());
    }

    @Override
    @Transactional
    public void deleteByCollection(String collectionName) {
        repository.deleteAllByViewNameStartingWith(collectionName + COLLECTION_VIEW_SEPARATOR);
    }

}