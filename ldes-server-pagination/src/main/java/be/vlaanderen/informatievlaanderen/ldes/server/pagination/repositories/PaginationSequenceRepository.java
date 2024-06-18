package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

import java.util.Optional;

public interface PaginationSequenceRepository {
    Optional<FragmentSequence> findLastProcessedSequence(ViewName viewName);

    void saveLastProcessedSequence(FragmentSequence sequence);

    void deleteByViewName(ViewName viewName);

    void deleteByCollection(String collectionName);
}
