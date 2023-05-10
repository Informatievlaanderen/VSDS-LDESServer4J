package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation.RetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RetentionPolicyCollectionImpl implements RetentionPolicyCollection {

	private final Map<ViewName, List<RetentionPolicy>> retentionPolicyMap;
	private final RetentionPolicyCreator retentionPolicyCreator;

	public RetentionPolicyCollectionImpl(RetentionPolicyCreator retentionPolicyCreator) {
		this.retentionPolicyCreator = retentionPolicyCreator;
		this.retentionPolicyMap = new HashMap<>();
	}

	public Map<ViewName, List<RetentionPolicy>> getRetentionPolicyMap() {
		return Map.copyOf(retentionPolicyMap);
	}

	@EventListener
	public void handleViewAddedEvent(ViewAddedEvent event) {
		retentionPolicyMap.put(event.getViewName(),
				retentionPolicyCreator.createRetentionPolicyListForView(event.getViewSpecification()));
	}

	@EventListener
	public void handleViewInitializationEvent(ViewInitializationEvent event) {
		retentionPolicyMap.put(event.getViewName(),
				retentionPolicyCreator.createRetentionPolicyListForView(event.getViewSpecification()));
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		retentionPolicyMap.remove(event.getViewName());
	}
}
