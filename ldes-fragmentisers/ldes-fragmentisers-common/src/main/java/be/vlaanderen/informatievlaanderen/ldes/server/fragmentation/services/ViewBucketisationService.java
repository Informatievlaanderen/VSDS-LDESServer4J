package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.ViewNeedsRebucketisationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ViewBucketisationService {
	private final ApplicationEventPublisher eventPublisher;
	private final Map<String, Boolean> fragmentationHasView = new HashMap<>();
	private final Map<String, Boolean> paginationHasView = new HashMap<>();

	public ViewBucketisationService(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public synchronized void setFragmentationHasView(ViewName view) {
		fragmentationHasView.put(view.asString(), true);
		checkAndLaunchEvent(view);
	}

	public synchronized void setFragmentationHasDeletedView(ViewName view) {
		fragmentationHasView.remove(view.asString());
	}

	public synchronized void setFragmentationHasDeletedCollection(String collectionName) {
		fragmentationHasView.keySet()
				.stream()
				.filter(viewName -> ViewName.fromString(viewName).getCollectionName().equals(collectionName))
				.toList()
				.forEach(fragmentationHasView::remove);
	}

	public synchronized void setPaginationHasView(ViewName view) {
		paginationHasView.put(view.asString(), true);
		checkAndLaunchEvent(view);
	}

	public synchronized void setPaginationHasDeletedView(ViewName view) {
		paginationHasView.remove(view.asString());
	}

	public synchronized void setPaginationHasDeletedCollection(String collectionName) {
		fragmentationHasView.keySet()
				.stream()
				.filter(viewName -> ViewName.fromString(viewName).getCollectionName().equals(collectionName))
				.forEach(fragmentationHasView::remove);
	}

	private void checkAndLaunchEvent(ViewName viewName) {
		if (fragmentationHasView.getOrDefault(viewName.asString(), false) &&
		    paginationHasView.getOrDefault(viewName.asString(), false)) {
			eventPublisher.publishEvent(new ViewNeedsRebucketisationEvent(viewName));
		}
	}
}
