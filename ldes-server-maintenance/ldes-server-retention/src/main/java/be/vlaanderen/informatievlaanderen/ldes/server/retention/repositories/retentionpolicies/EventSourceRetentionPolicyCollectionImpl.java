package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DeletionPolicyChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.EventSourceLevelRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyFactory;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EventSourceRetentionPolicyCollectionImpl implements RetentionPolicyCollection<EventSourceLevelRetentionPolicy> {
	private final RetentionPolicyFactory retentionPolicyFactory;
	private final Set<EventSourceLevelRetentionPolicy> retentionPolicies;

	public EventSourceRetentionPolicyCollectionImpl(RetentionPolicyFactory retentionPolicyFactory) {
		this.retentionPolicyFactory = retentionPolicyFactory;
		retentionPolicies = new HashSet<>();
	}

	@Override
	public Set<EventSourceLevelRetentionPolicy> getRetentionPolicies() {
		return Set.copyOf(retentionPolicies);
	}

	@EventListener
	public void handleDeletionPolicyChangedEvent(DeletionPolicyChangedEvent event) {
		removeFromCollection(event.collectionName());
		addToCollection(event.collectionName(), event.retentionPolicies());
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		removeFromCollection(event.collectionName());
	}

	private void removeFromCollection(String collectionName) {
		retentionPolicies.removeIf(policy -> policy.collectionName().equals(collectionName));
	}

	private void addToCollection(String collectionName, List<Model> retentionPolicyModels) {
		if (!retentionPolicyModels.isEmpty()) {
			retentionPolicyFactory
					.extractRetentionPolicy(retentionPolicyModels)
					.map(retentionPolicy -> new EventSourceLevelRetentionPolicy(collectionName, retentionPolicy))
					.ifPresent(retentionPolicies::add);
		}
	}
}
