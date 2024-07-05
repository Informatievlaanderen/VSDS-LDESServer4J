package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import org.apache.jena.rdf.model.Model;

import java.util.List;

public record DeletionPolicyChangedEvent(String collectionName, List<Model> retentionPolicies) {
}
