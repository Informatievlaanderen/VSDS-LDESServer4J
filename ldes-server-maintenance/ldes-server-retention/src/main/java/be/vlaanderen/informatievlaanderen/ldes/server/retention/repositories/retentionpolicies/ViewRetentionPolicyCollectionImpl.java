package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component(value = "viewRetentionPolicyCollection")
public class ViewRetentionPolicyCollectionImpl implements ViewRetentionPolicyCollection {

    private final Map<ViewName, RetentionPolicy> retentionPolicies;
    private final RetentionPolicyFactory retentionPolicyFactory;

    public ViewRetentionPolicyCollectionImpl(RetentionPolicyFactory retentionPolicyFactory) {
        this.retentionPolicyFactory = retentionPolicyFactory;
        this.retentionPolicies = new HashMap<>();
    }

    @EventListener(classes = {ViewInitializationEvent.class, ViewAddedEvent.class})
    public void handleViewAddedEvent(ViewSupplier event) {
        addToMap(event.viewSpecification());
    }

    @EventListener
    public void handleViewDeletedEvent(ViewDeletedEvent event) {
        retentionPolicies.remove(event.getViewName());
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        retentionPolicies.keySet().stream()
                .filter(viewName -> viewName.getCollectionName().equals(event.collectionName()))
                .toList()
                .forEach(retentionPolicies::remove);
    }

    @Override
    public Map<ViewName, RetentionPolicy> getRetentionPolicies() {
        return Map.copyOf(retentionPolicies);
    }

    @Override
    public boolean isEmpty() {
        return retentionPolicies.isEmpty();
    }

    private void addToMap(ViewSpecification viewSpecification) {
        retentionPolicyFactory
                .extractRetentionPolicy(viewSpecification)
                .ifPresent(policy -> retentionPolicies.put(viewSpecification.getName(), policy));
    }

}
