package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.v2.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.PostgresAdminConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.v2.entity.EventSourceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;

import java.util.List;

public class EventSourceMapper {
    private EventSourceMapper() {
    }

    public static EventSource fromEntity(EventSourceEntity eventSourceEntity) {
        return new EventSource(eventSourceEntity.getCollectionName(), eventSourceEntity.getRetentionPolicies());
    }
}
