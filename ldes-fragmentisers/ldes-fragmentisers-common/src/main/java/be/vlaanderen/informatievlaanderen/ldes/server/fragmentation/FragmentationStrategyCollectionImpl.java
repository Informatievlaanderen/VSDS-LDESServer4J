package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberUnallocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FragmentationStrategyCollectionImpl implements FragmentationStrategyCollection {

	private final Map<ViewName, FragmentationStrategy> fragmentationStrategyMap;
	private final RootFragmentCreator rootFragmentCreator;
	private final FragmentationStrategyCreator fragmentationStrategyCreator;
	private final RefragmentationService refragmentationService;
	private final FragmentRepository fragmentRepository;
	private final AllocationRepository allocationRepository;

	public FragmentationStrategyCollectionImpl(
			RootFragmentCreator rootFragmentCreator, FragmentationStrategyCreator fragmentationStrategyCreator,
			RefragmentationService refragmentationService,
			FragmentRepository fragmentRepository, AllocationRepository allocationRepository) {
		this.fragmentRepository = fragmentRepository;
		this.allocationRepository = allocationRepository;
		this.fragmentationStrategyMap = new HashMap<>();
		this.rootFragmentCreator = rootFragmentCreator;
		this.fragmentationStrategyCreator = fragmentationStrategyCreator;
		this.refragmentationService = refragmentationService;
	}

	public Map<ViewName, FragmentationStrategy> getFragmentationStrategyMap() {
		return Map.copyOf(fragmentationStrategyMap);
	}

	@EventListener
	public void handleViewAddedEvent(ViewAddedEvent event) {
		Fragment rootFragmentForView = rootFragmentCreator.createRootFragmentForView(event.getViewName());
		FragmentationStrategy fragmentationStrategyForView = fragmentationStrategyCreator
				.createFragmentationStrategyForView(event.getViewSpecification());
		refragmentationService.refragmentMembersForView(rootFragmentForView, fragmentationStrategyForView);
		fragmentationStrategyMap.put(event.getViewName(),
				fragmentationStrategyForView);
	}

	@EventListener
	public void handleViewInitializationEvent(ViewInitializationEvent event) {
		FragmentationStrategy fragmentationStrategyForView = fragmentationStrategyCreator
				.createFragmentationStrategyForView(event.getViewSpecification());
		fragmentationStrategyMap.put(event.getViewName(),
				fragmentationStrategyForView);
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		fragmentRepository.deleteTreeNodesByCollection(event.collectionName());
		allocationRepository.unallocateMembersFromCollection(event.collectionName());
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		fragmentRepository.removeLdesFragmentsOfView(event.getViewName().asString());
		allocationRepository.unallocateAllMembersFromView(event.getViewName());
		fragmentationStrategyMap.remove(event.getViewName());
	}

	@EventListener
	public void handleMemberUnallocatedEvent(MemberUnallocatedEvent event) {
		allocationRepository.unallocateMemberFromView(event.memberId(), event.viewName());
	}

}
