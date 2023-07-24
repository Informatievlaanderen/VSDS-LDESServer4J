package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberUnallocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyExecutorCreatorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FragmentationStrategyCollectionImpl implements FragmentationStrategyCollection {

	private final RefragmentationService refragmentationService;
	private final FragmentRepository fragmentRepository;
	private final AllocationRepository allocationRepository;
	private final Set<FragmentationStrategyExecutor> fragmentationStrategySet;
	private final FragmentationStrategyCreator fragmentationStrategyCreator;
	private final FragmentationStrategyExecutorCreatorImpl fragmentationStrategyExecutorCreator;

	public FragmentationStrategyCollectionImpl(
			FragmentationStrategyCreator fragmentationStrategyCreator,
			RefragmentationService refragmentationService,
			FragmentRepository fragmentRepository, AllocationRepository allocationRepository,
			FragmentationStrategyExecutorCreatorImpl fragmentationStrategyExecutorCreator) {
		this.fragmentRepository = fragmentRepository;
		this.allocationRepository = allocationRepository;
		this.fragmentationStrategyExecutorCreator = fragmentationStrategyExecutorCreator;
		this.fragmentationStrategySet = new HashSet<>();
		this.fragmentationStrategyCreator = fragmentationStrategyCreator;
		this.refragmentationService = refragmentationService;
	}

	public List<FragmentationStrategyExecutor> getFragmentationStrategyExecutors(String collectionName) {
		return fragmentationStrategySet
				.stream()
				.filter(executor -> executor.isPartOfCollection(collectionName))
				.toList();
	}

	@Override
	public List<ViewName> getViews(String collectionName) {
		return fragmentationStrategySet
				.stream()
				.filter(executor -> executor.isPartOfCollection(collectionName))
				.map(FragmentationStrategyExecutor::getViewName)
				.toList();
	}

	// TODO TVB: 20/07/23 test
	@EventListener
	public void handleViewAddedEvent(ViewAddedEvent event) {
		final ViewName viewName = event.getViewName();
		final FragmentationStrategy fragmentationStrategy = fragmentationStrategyCreator
				.createFragmentationStrategyForView(event.getViewSpecification());
		refragmentationService.refragmentMembersForView(viewName, fragmentationStrategy);
		final var fragmentationStrategyExecutor = fragmentationStrategyExecutorCreator.createExecutor(viewName,
				fragmentationStrategy);
		fragmentationStrategySet.add(fragmentationStrategyExecutor);
	}

	// TODO TVB: 20/07/23 test
	@EventListener
	public void handleViewInitializationEvent(ViewInitializationEvent event) {
		final FragmentationStrategy fragmentationStrategy = fragmentationStrategyCreator
				.createFragmentationStrategyForView(event.getViewSpecification());
		final var fragmentationStrategyExecutor = fragmentationStrategyExecutorCreator
				.createExecutor(event.getViewName(), fragmentationStrategy);
		fragmentationStrategySet.add(fragmentationStrategyExecutor);
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		fragmentRepository.deleteTreeNodesByCollection(event.collectionName());
		allocationRepository.unallocateMembersFromCollection(event.collectionName());
	}

	// TODO TVB: 24/07/23 test
	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		fragmentRepository.removeLdesFragmentsOfView(event.getViewName().asString());
		allocationRepository.unallocateAllMembersFromView(event.getViewName());
		fragmentationStrategySet
				.stream()
				.filter(executor -> executor.getViewName().equals(event.getViewName()))
				.findFirst()
				.ifPresent(fragmentationStrategySet::remove);
	}

	// TODO TVB: 24/07/23 is this tested?
	@EventListener
	public void handleMemberUnallocatedEvent(MemberUnallocatedEvent event) {
		allocationRepository.unallocateMemberFromView(event.memberId(), event.viewName());
	}

}
