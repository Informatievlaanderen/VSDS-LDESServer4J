package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.ViewNeedsRebucketisationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ViewBucketisationService {
	private final ApplicationEventPublisher eventPublisher;
	private final Set<String> fragmentationHasView = new HashSet<>();
	private final Set<String> paginationHasView = new HashSet<>();

	public ViewBucketisationService(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public synchronized void setHasView(@NotNull ViewName view, @NotNull ServiceType serviceType) {
		if (serviceType.equals(ServiceType.FRAGMENTATION)) {
			fragmentationHasView.add(view.asString());
		} else {
			paginationHasView.add(view.asString());
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
			fragmentationHasView.removeIf(viewName -> ViewName.fromString(viewName)
					.getCollectionName()
					.equals(collectionName));
		} else {
			paginationHasView.removeIf(viewName -> ViewName.fromString(viewName)
					.getCollectionName()
					.equals(collectionName));
		}


	}

	private void checkAndLaunchEvent(ViewName viewName) {
		if (fragmentationHasView.contains(viewName.asString()) && paginationHasView.contains(viewName.asString())) {
			eventPublisher.publishEvent(new ViewNeedsRebucketisationEvent(viewName));
		}
	}

	public enum ServiceType {
		PAGINATION, FRAGMENTATION
	}
}
