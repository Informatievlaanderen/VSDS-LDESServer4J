package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

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
public class DeletionPolicyCollectionImpl implements DeletionPolicyCollection {
    private final RetentionPolicyFactory retentionPolicyFactory;
    private final Map<String, RetentionPolicy> eventSourceRetentionPolicyMap;

    public DeletionPolicyCollectionImpl(RetentionPolicyFactory retentionPolicyFactory) {
        this.retentionPolicyFactory = retentionPolicyFactory;
        this.eventSourceRetentionPolicyMap = new HashMap<>();
    }
    @Override
    public Map<String, RetentionPolicy> getEventSourceRetentionPolicyMap() {
        return Map.copyOf(eventSourceRetentionPolicyMap);
    }

    @Override
    public boolean isEmpty() {
        return eventSourceRetentionPolicyMap.isEmpty();
    }

    @EventListener
    public void handleDeletionPolicyChangedEvent(DeletionPolicyChangedEvent event) {
        eventSourceRetentionPolicyMap.remove(event.collectionName());
        addToMap(event.collectionName(), event.retentionPolicies());
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        eventSourceRetentionPolicyMap.remove(event.collectionName());
    }

    private void addToMap(String collectionName, List<Model> retentionPolicyModels) {
        if (!retentionPolicyModels.isEmpty()) {
            retentionPolicyFactory
                    .extractRetentionPolicy(retentionPolicyModels)
                    .ifPresent(policy -> eventSourceRetentionPolicyMap.put(collectionName, policy));
        }
    }
}
