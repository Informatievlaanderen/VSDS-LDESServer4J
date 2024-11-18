package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Objects;

public record EventSource(String collectionName, List<Model> retentionPolicies) {

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EventSource eventSource = (EventSource) o;
        return collectionName.equals(eventSource.collectionName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionName);
    }
}
