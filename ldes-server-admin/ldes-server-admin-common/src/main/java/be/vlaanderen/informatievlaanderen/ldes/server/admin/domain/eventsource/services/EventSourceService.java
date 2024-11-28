package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Optional;

public interface EventSourceService {
    Optional<EventSource> getEventSource(String collectionName);

    void updateEventSource(String collectionName, List<Model> retentionPolicies);
}
