package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DeletionPolicyChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventSourceRetentionPolicyCollectionImpl implements RetentionPolicyCollection<String> {
	private final RetentionPolicyFactory retentionPolicyFactory;
	private final Map<String, RetentionPolicy> retentionPolicies;

	public EventSourceRetentionPolicyCollectionImpl(RetentionPolicyFactory retentionPolicyFactory) {
		this.retentionPolicyFactory = retentionPolicyFactory;
		retentionPolicies = new HashMap<>();
	}

	@Override
	public Map<String, RetentionPolicy> getRetentionPolicies() {
		return Map.copyOf(retentionPolicies);
	}

	@Override
	public boolean isEmpty() {
		return retentionPolicies.isEmpty();
	}

	@EventListener
	public void handleDeletionPolicyChangedEvent(DeletionPolicyChangedEvent event) {
		retentionPolicies.remove(event.collectionName());
		addToMap(event.collectionName(), event.retentionPolicies());
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		retentionPolicies.remove(event.collectionName());
	}

	private void addToMap(String collectionName, List<Model> retentionPolicyModels) {
		if (!retentionPolicyModels.isEmpty()) {
			retentionPolicyFactory
					.extractRetentionPolicy(retentionPolicyModels)
					.ifPresent(policy -> retentionPolicies.put(collectionName, policy));
		}
	}
}
