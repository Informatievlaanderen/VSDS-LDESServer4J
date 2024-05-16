package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Objects;

public class EventSource {
    private final String collectionName;
    private final List<Model> retentionPolicies;

    public EventSource(String collectionName, List<Model> retentionPolicies) {
        this.collectionName = collectionName;
        this.retentionPolicies = retentionPolicies;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public List<Model> getRetentionPolicies() {
        return retentionPolicies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EventSource eventSource = (EventSource) o;
        return collectionName.equals(eventSource.getCollectionName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionName);
    }
}
