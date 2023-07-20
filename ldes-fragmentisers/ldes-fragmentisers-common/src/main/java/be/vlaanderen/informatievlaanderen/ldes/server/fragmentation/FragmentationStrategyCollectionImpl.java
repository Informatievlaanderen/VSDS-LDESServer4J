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
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MembersToFragmentRepository;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FragmentationStrategyCollectionImpl implements FragmentationStrategyCollection {

	// TODO TVB: 20/07/23 to be remove
	private final Map<ViewName, FragmentationStrategy> fragmentationStrategyMap;
	// TODO TVB: 20/07/23 extract to refragment service
	private final RootFragmentCreator rootFragmentCreator;
	private final FragmentationStrategyCreator fragmentationStrategyCreator;
	private final RefragmentationService refragmentationService;
	private final FragmentRepository fragmentRepository;
	private final AllocationRepository allocationRepository;
	// TODO TVB: 20/07/23 should replace fragmentationStratMap
	private final List<FragmentationStrategyExecutor> myList = new ArrayList<>();
	private final ObservationRegistry observationRegistry;
	private final MembersToFragmentRepository membersToFragmentRepository;

	public FragmentationStrategyCollectionImpl(
			RootFragmentCreator rootFragmentCreator, FragmentationStrategyCreator fragmentationStrategyCreator,
			RefragmentationService refragmentationService,
			FragmentRepository fragmentRepository, AllocationRepository allocationRepository,
			ObservationRegistry observationRegistry, MembersToFragmentRepository membersToFragmentRepository) {
		this.fragmentRepository = fragmentRepository;
		this.allocationRepository = allocationRepository;
		this.observationRegistry = observationRegistry;
		this.membersToFragmentRepository = membersToFragmentRepository;
		this.fragmentationStrategyMap = new HashMap<>();
		this.rootFragmentCreator = rootFragmentCreator;
		this.fragmentationStrategyCreator = fragmentationStrategyCreator;
		this.refragmentationService = refragmentationService;
	}

	public Map<ViewName, FragmentationStrategy> getFragmentationStrategyMap() {
		return Map.copyOf(fragmentationStrategyMap);
	}

	public List<FragmentationStrategyExecutor> getFragmentationStrategyExecutors() {
		return myList;
	}

	@Override
	public List<ViewName> getViews(String collectionName) {
		return fragmentationStrategyMap
				.keySet()
				.stream()
				.filter(viewName -> Objects.equals(collectionName, viewName.getCollectionName()))
				.toList();
	}

	// TODO TVB: 20/07/23 update test
	@EventListener
	public void handleViewAddedEvent(ViewAddedEvent event) {
		Fragment rootFragmentForView = rootFragmentCreator.createRootFragmentForView(event.getViewName());
		FragmentationStrategy fragmentationStrategyForView = fragmentationStrategyCreator
				.createFragmentationStrategyForView(event.getViewSpecification());
		refragmentationService.refragmentMembersForView(rootFragmentForView, fragmentationStrategyForView);
		fragmentationStrategyMap.put(event.getViewName(), fragmentationStrategyForView);
		// TODO TVB: 20/07/23 cleanup
		RootFragmentRetriever rootFragmentRetriever = new RootFragmentRetriever(fragmentRepository,
				observationRegistry);
		myList.add(new FragmentationStrategyExecutor(event.getViewName(), fragmentationStrategyForView,
				rootFragmentRetriever, observationRegistry, membersToFragmentRepository));
	}

	// TODO TVB: 20/07/23 update test
	@EventListener
	public void handleViewInitializationEvent(ViewInitializationEvent event) {
		FragmentationStrategy fragmentationStrategyForView = fragmentationStrategyCreator
				.createFragmentationStrategyForView(event.getViewSpecification());
		fragmentationStrategyMap.put(event.getViewName(), fragmentationStrategyForView);
		// TODO TVB: 20/07/23 cleanup
		RootFragmentRetriever rootFragmentRetriever = new RootFragmentRetriever(fragmentRepository,
				observationRegistry);
		myList.add(new FragmentationStrategyExecutor(event.getViewName(), fragmentationStrategyForView,
				rootFragmentRetriever, observationRegistry, membersToFragmentRepository));
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
