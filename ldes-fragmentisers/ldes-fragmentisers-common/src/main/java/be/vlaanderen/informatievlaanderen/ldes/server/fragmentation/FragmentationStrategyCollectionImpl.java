package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberUnallocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
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

	private final FragmentRepository fragmentRepository;
	private final AllocationRepository allocationRepository;
	private final Set<FragmentationStrategyExecutor> fragmentationStrategySet;
	private final FragmentationStrategyExecutorCreatorImpl fragmentationStrategyExecutorCreator;

	public FragmentationStrategyCollectionImpl(
			FragmentRepository fragmentRepository, AllocationRepository allocationRepository,
			FragmentationStrategyExecutorCreatorImpl fragmentationStrategyExecutorCreator) {
		this.fragmentRepository = fragmentRepository;
		this.allocationRepository = allocationRepository;
		this.fragmentationStrategyExecutorCreator = fragmentationStrategyExecutorCreator;
		this.fragmentationStrategySet = new HashSet<>();
	}

	public List<FragmentationStrategyExecutor> getFragmentationStrategyExecutors(String collectionName) {
		return fragmentationStrategySet
				.stream()
				.filter(executor -> executor.isPartOfCollection(collectionName))
				.toList();
	}

	@EventListener
	public void handleViewAddedEvent(ViewAddedEvent event) {
		prepareFragmentationStrategyExecutor(event.getViewName(), event.getViewSpecification());
	}

	@EventListener
	public void handleViewInitializationEvent(ViewInitializationEvent event) {
		prepareFragmentationStrategyExecutor(event.getViewName(), event.getViewSpecification());
	}

	private void prepareFragmentationStrategyExecutor(ViewName viewName, ViewSpecification viewSpecification) {
		final var fragmentationStrategyExecutor = fragmentationStrategyExecutorCreator.createExecutor(viewName,
				viewSpecification);
		fragmentationStrategySet.add(fragmentationStrategyExecutor);
		fragmentationStrategyExecutor.execute();
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
		fragmentationStrategySet
				.stream()
				.filter(executor -> executor.getViewName().equals(event.getViewName()))
				.findFirst()
				.ifPresent(fragmentationStrategySet::remove);
	}

	@EventListener
	public void handleMemberUnallocatedEvent(MemberUnallocatedEvent event) {
		allocationRepository.unallocateMemberFromView(event.memberId(), event.viewName());
	}

}
