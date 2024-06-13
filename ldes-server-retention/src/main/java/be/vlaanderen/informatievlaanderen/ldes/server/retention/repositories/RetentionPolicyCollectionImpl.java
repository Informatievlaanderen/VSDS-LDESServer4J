package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RetentionPolicyCollectionImpl implements RetentionPolicyCollection {

    private final Map<ViewName, RetentionPolicy> retentionPolicyMap;
    private final RetentionPolicyFactory retentionPolicyFactory;

    public RetentionPolicyCollectionImpl(RetentionPolicyFactory retentionPolicyFactory) {
        this.retentionPolicyFactory = retentionPolicyFactory;
        this.retentionPolicyMap = new HashMap<>();
    }

    @EventListener
    public void handleViewAddedEvent(ViewAddedEvent event) {
        addToMap(event.getViewName(), event.viewSpecification());
    }

    @EventListener
    public void handleViewInitializationEvent(ViewInitializationEvent event) {
        addToMap(event.getViewName(), event.viewSpecification());
    }

    @EventListener
    public void handleViewDeletedEvent(ViewDeletedEvent event) {
        retentionPolicyMap.remove(event.getViewName());
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        retentionPolicyMap.keySet().stream()
                .filter(viewName -> viewName.getCollectionName().equals(event.collectionName()))
                .toList()
                .forEach(retentionPolicyMap::remove);
    }

    @Override
    public Map<ViewName, RetentionPolicy> getRetentionPolicyMap() {
        return Map.copyOf(retentionPolicyMap);
    }

    @Override
    public boolean isEmpty() {
        return retentionPolicyMap.isEmpty();
    }

    private void addToMap(ViewName viewName, ViewSpecification viewSpecification) {
        retentionPolicyFactory
                .extractRetentionPolicy(viewSpecification)
                .ifPresent(policy -> retentionPolicyMap.put(viewName, policy));
    }

}
