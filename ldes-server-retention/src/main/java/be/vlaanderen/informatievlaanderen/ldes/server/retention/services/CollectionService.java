package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.Collection;

public interface CollectionService {
    Collection getCollection(String collectionName);
}
