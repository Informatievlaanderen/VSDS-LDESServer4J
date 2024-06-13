package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.ViewNeedsRebucketisationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.jetbrains.annotations.NotNull;
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

	public synchronized void setHasView(@NotNull ViewName view, @NotNull ServiceType serviceType) {
		if (serviceType.equals(ServiceType.FRAGMENTATION)) {
			fragmentationHasView.put(view.asString(), true);
		} else {
			paginationHasView.put(view.asString(), true);
		}
		checkAndLaunchEvent(view);
	}

	public synchronized void setDeletedView(@NotNull ViewName view, @NotNull ServiceType serviceType) {
		if (serviceType.equals(ServiceType.FRAGMENTATION)) {
			fragmentationHasView.remove(view.asString());
		} else {
			paginationHasView.remove(view.asString());
		}

	}

	public synchronized void setDeletedCollection(@NotNull String collectionName,
	                                              @NotNull ServiceType serviceType) {
		if (serviceType.equals(ServiceType.FRAGMENTATION)) {
			fragmentationHasView.keySet()
					.stream()
					.filter(viewName -> ViewName.fromString(viewName).getCollectionName().equals(collectionName))
					.toList()
					.forEach(fragmentationHasView::remove);
		} else {
			paginationHasView.keySet()
					.stream()
					.filter(viewName -> ViewName.fromString(viewName).getCollectionName().equals(collectionName))
					.toList()
					.forEach(paginationHasView::remove);
		}


	}

	private void checkAndLaunchEvent(ViewName viewName) {
		if (fragmentationHasView.getOrDefault(viewName.asString(), false) &&
		    paginationHasView.getOrDefault(viewName.asString(), false)) {
			eventPublisher.publishEvent(new ViewNeedsRebucketisationEvent(viewName));
		}
	}

	public enum ServiceType {
		PAGINATION, FRAGMENTATION
	}
}
