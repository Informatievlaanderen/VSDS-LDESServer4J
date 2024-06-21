package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import org.apache.jena.rdf.model.Model;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public final class DeletionPolicyChangedEvent extends ApplicationEvent {
	private final String collectionName;
	private final List<Model> retentionPolicies;

	public DeletionPolicyChangedEvent(Object source, String collectionName, List<Model> retentionPolicies) {
		super(source);
		this.collectionName = collectionName;
		this.retentionPolicies = retentionPolicies;
	}

	public String collectionName() {
		return collectionName;
	}

	public List<Model> retentionPolicies() {
		return retentionPolicies;
	}
}
