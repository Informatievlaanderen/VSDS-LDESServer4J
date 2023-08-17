package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyExecutorCreatorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

@Component
public class FragmentationStrategyCollectionImpl implements FragmentationStrategyCollection {

	private final FragmentRepository fragmentRepository;
	private final Set<FragmentationStrategyExecutor> fragmentationStrategySet;
	private final FragmentationStrategyExecutorCreatorImpl fragmentationStrategyExecutorCreator;
	private final FragmentSequenceRepository fragmentSequenceRepository;

	public FragmentationStrategyCollectionImpl(
			FragmentRepository fragmentRepository,
			FragmentationStrategyExecutorCreatorImpl fragmentationStrategyExecutorCreator,
			FragmentSequenceRepository fragmentSequenceRepository) {
		this.fragmentRepository = fragmentRepository;
		this.fragmentationStrategyExecutorCreator = fragmentationStrategyExecutorCreator;
		this.fragmentSequenceRepository = fragmentSequenceRepository;
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
		removeFromStrategySet(
				executor -> Objects.equals(executor.getViewName().getCollectionName(), event.collectionName()));
		fragmentRepository.deleteTreeNodesByCollection(event.collectionName());
		fragmentSequenceRepository.deleteByCollection(event.collectionName());
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		removeFromStrategySet(executor -> Objects.equals(executor.getViewName(), event.getViewName()));
		fragmentRepository.removeLdesFragmentsOfView(event.getViewName().asString());
		fragmentSequenceRepository.deleteByViewName(event.getViewName());
	}

	private void removeFromStrategySet(Predicate<FragmentationStrategyExecutor> filterPredicate) {
		fragmentationStrategySet
				.stream()
				.filter(filterPredicate)
				.toList()
				.forEach(executor -> {
					executor.shutdown();
					fragmentationStrategySet.remove(executor);
				});
	}

}
