package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.ViewBucketisationService;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.ViewBucketisationService.ServiceType.FRAGMENTATION;

@Component
public class FragmentationStrategyBatchCollection implements FragmentationStrategyCollection {

	private final FragmentRepository fragmentRepository;
	private final Set<FragmentationStrategyBatchExecutor> fragmentationStrategySet;
	private final BucketisedMemberRepository bucketisedMemberRepository;
	private final FragmentationStrategyCreator fragmentationStrategyCreator;
	private final ViewBucketisationService viewBucketisationService;
	private final ObservationRegistry observationRegistry;

	public FragmentationStrategyBatchCollection(
			FragmentRepository fragmentRepository,
			BucketisedMemberRepository bucketisedMemberRepository,
			FragmentationStrategyCreator fragmentationStrategyCreator,
			ViewBucketisationService viewBucketisationService,
			ObservationRegistry observationRegistry) {
		this.fragmentRepository = fragmentRepository;
		this.bucketisedMemberRepository = bucketisedMemberRepository;
		this.fragmentationStrategyCreator = fragmentationStrategyCreator;
		this.viewBucketisationService = viewBucketisationService;
		this.observationRegistry = observationRegistry;
		this.fragmentationStrategySet = new HashSet<>();
	}

	@Override
	public List<FragmentationStrategyBatchExecutor> getAllFragmentationStrategyExecutors(String collectionName) {
		return fragmentationStrategySet
				.stream()
				.filter(executor -> executor.isPartOfCollection(collectionName))
				.toList();
	}

	@Override
	public Optional<FragmentationStrategyBatchExecutor> getFragmentationStrategyExecutor(String viewName) {
		return fragmentationStrategySet.stream()
				.filter(fragmentationStrategyBatchExecutor ->
						fragmentationStrategyBatchExecutor.getViewName()
								.asString()
								.equals(viewName))
				.findFirst();
	}

	@EventListener({ViewAddedEvent.class, ViewInitializationEvent.class})
	public void handleViewAddedEvent(ViewSupplier event) {
		final var fragmentationStrategyExecutor = createExecutor(event.viewSpecification().getName(), event.viewSpecification());
		fragmentationStrategySet.add(fragmentationStrategyExecutor);
		viewBucketisationService.setHasView(event.viewSpecification().getName(), FRAGMENTATION);
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		removeFromStrategySet(
				executor -> Objects.equals(executor.getViewName().getCollectionName(), event.collectionName()));
		fragmentRepository.deleteTreeNodesByCollection(event.collectionName());
		bucketisedMemberRepository.deleteByCollection(event.collectionName());
		viewBucketisationService.setDeletedCollection(event.collectionName(), FRAGMENTATION);
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		removeFromStrategySet(executor -> Objects.equals(executor.getViewName(), event.getViewName()));
		fragmentRepository.removeLdesFragmentsOfView(event.getViewName().asString());
		bucketisedMemberRepository.deleteByViewName(event.getViewName());
		viewBucketisationService.setDeletedView(event.getViewName(), FRAGMENTATION);
	}

	private void removeFromStrategySet(Predicate<FragmentationStrategyBatchExecutor> filterPredicate) {
		fragmentationStrategySet
				.stream()
				.filter(filterPredicate)
				.toList()
				.forEach(fragmentationStrategySet::remove);
	}

	private FragmentationStrategyBatchExecutor createExecutor(ViewName viewName,
	                                                    ViewSpecification viewSpecification) {
		final FragmentationStrategy fragmentationStrategy = fragmentationStrategyCreator
				.createFragmentationStrategyForView(viewSpecification);
		final var rootFragmentRetriever = new RootFragmentRetriever(fragmentRepository, observationRegistry);
		return new FragmentationStrategyBatchExecutor(viewName, fragmentationStrategy, rootFragmentRetriever, observationRegistry);
	}
}
