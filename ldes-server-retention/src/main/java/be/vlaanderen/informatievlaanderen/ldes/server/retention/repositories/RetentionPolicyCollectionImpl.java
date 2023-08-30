package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RetentionPolicyCollectionImpl implements RetentionPolicyCollection {

	private final Map<String, List<RetentionPolicy>> retentionPolicyMap;
	private final RetentionPolicyFactory retentionPolicyFactory;

	public RetentionPolicyCollectionImpl(RetentionPolicyFactory retentionPolicyFactory) {
		this.retentionPolicyFactory = retentionPolicyFactory;
		this.retentionPolicyMap = new HashMap<>();
	}

	public Map<String, List<RetentionPolicy>> getRetentionPolicyMap() {
		return Map.copyOf(retentionPolicyMap);
	}

	@EventListener
	public void handleViewAddedEvent(ViewAddedEvent event) {
		retentionPolicyMap.put(event.getViewName().asString(),
				retentionPolicyFactory.getRetentionPolicyListForView(event.getViewSpecification()));
	}

	@EventListener
	public void handleViewInitializationEvent(ViewInitializationEvent event) {
		retentionPolicyMap.put(event.getViewName().asString(),
				retentionPolicyFactory.getRetentionPolicyListForView(event.getViewSpecification()));
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		retentionPolicyMap.remove(event.getViewName().asString());
	}
}
