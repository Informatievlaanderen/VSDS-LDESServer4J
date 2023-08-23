package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

public interface PaginationStartingNodeIterator {
    Fragment next();
    boolean hasNext();
}
