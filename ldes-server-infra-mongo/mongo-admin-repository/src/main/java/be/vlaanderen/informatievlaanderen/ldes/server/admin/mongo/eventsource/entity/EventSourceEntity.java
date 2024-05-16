package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventsource.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "eventsource")
public class EventSourceEntity {
    @Id
    private final String collectionName;
    private final List<String> retentionPolicies;

    public EventSourceEntity(String collectionName, List<String> retentionPolicies) {
        this.collectionName = collectionName;
        this.retentionPolicies = retentionPolicies;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public List<String> getRetentionPolicies() {
        return retentionPolicies;
    }
}
