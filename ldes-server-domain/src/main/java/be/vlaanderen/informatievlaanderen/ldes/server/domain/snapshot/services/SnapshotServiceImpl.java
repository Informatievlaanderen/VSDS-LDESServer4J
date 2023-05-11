package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.exception.SnapshotCreationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository.SnapshotRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class SnapshotServiceImpl implements SnapshotService {

	private final SnapShotCreator snapShotCreator;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final SnapshotRepository snapshotRepository;
	private final SnapshotRelationLinker snapshotRelationLinker;
	private final EventStreamService eventStreamService;

	public SnapshotServiceImpl(SnapShotCreator snapShotCreator, LdesFragmentRepository ldesFragmentRepository,
			SnapshotRepository snapshotRepository, SnapshotRelationLinker snapshotRelationLinker,
			EventStreamService eventStreamService) {
		this.snapShotCreator = snapShotCreator;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.snapshotRepository = snapshotRepository;
		this.snapshotRelationLinker = snapshotRelationLinker;
		this.eventStreamService = eventStreamService;
	}

	@Override
	public void createSnapshot(String collectionName) {
		Optional<Snapshot> lastSnapshot = retrieveLastSnapshot();

		EventStreamResponse eventStream = eventStreamService.retrieveEventStream(collectionName);

		ViewName viewName = eventStream.getViews().stream()
				.map(ViewSpecification::getName)
				.filter(name -> name.equals(new ViewName(collectionName, ViewConfig.DEFAULT_VIEW_NAME)))
				.findFirst()
				.orElseThrow(() -> new SnapshotCreationException(
						"No default pagination view configured for collection " + collectionName));

		List<LdesFragment> treeNodesForSnapshot;
		if (lastSnapshot.isPresent()) {
			treeNodesForSnapshot = getTreeNodesForSnapshotFromPreviousSnapshot(viewName, lastSnapshot.get());
		} else {
			treeNodesForSnapshot = ldesFragmentRepository.retrieveFragmentsOfView(viewName.asString()).toList();
		}

		if (treeNodesForSnapshot.isEmpty()) {
			throw new SnapshotCreationException(
					"No TreeNodes available in view " + viewName.asString() + " which is used for snapshotting");
		}
		createSnapshotForTreeNodes(treeNodesForSnapshot, eventStream);
	}

	@Override
	public void deleteSnapshot(String collectionName) {
		snapshotRepository.deleteSnapshotsByCollectionName(collectionName);
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		deleteSnapshot(event.collectionName());
	}

	private void createSnapshotForTreeNodes(List<LdesFragment> treeNodesForSnapshot,
			EventStreamResponse eventStream) {
		Snapshot snapshot = snapShotCreator.createSnapshotForTreeNodes(treeNodesForSnapshot, eventStream);
		LdesFragment lastTreeNodeOfSnapshot = snapshotRelationLinker.addRelationsToUncoveredTreeNodes(snapshot,
				treeNodesForSnapshot);
		ldesFragmentRepository.saveFragment(lastTreeNodeOfSnapshot);
		snapshotRepository.saveSnapShot(snapshot);
	}

	private Optional<Snapshot> retrieveLastSnapshot() {
		return snapshotRepository.getLastSnapshot();
	}

	private Optional<String> getNextFragmentFromSnapshot(List<LdesFragment> treeNodesOfSnapshot) {
		return treeNodesOfSnapshot.stream().filter(ldesFragment -> !ldesFragment.isRoot())
				.filter(ldesFragment -> !ldesFragment.isImmutable())
				.map(LdesFragment::getRelations)
				.flatMap(List::stream)
				.map(TreeRelation::treeNode)
				.findFirst();
	}

	private List<LdesFragment> getTreeNodesForSnapshotFromPreviousSnapshot(ViewName viewName, Snapshot lastSnapshot) {
		List<LdesFragment> treeNodesOfSnapshot = ldesFragmentRepository
				.retrieveFragmentsOfView(lastSnapshot.getSnapshotId())
				.filter(ldesFragment -> !ldesFragment.isRoot()).toList();
		Stream<LdesFragment> treeNodesOfDefaultView = ldesFragmentRepository
				.retrieveFragmentsOfView(viewName.asString());
		String lastFragment = getNextFragmentFromSnapshot(treeNodesOfSnapshot)
				.orElseThrow(() -> new SnapshotCreationException(
						"First fragment of " + viewName.asString() + " after previous snapshot "
								+ lastSnapshot.getSnapshotId() + " could not be found"));
		List<LdesFragment> relevantTreeNodesOfDefaultView = treeNodesOfDefaultView
				.filter(ldesFragment -> !ldesFragment.isRoot()).filter(new GreaterOrEqualsPageFilter(lastFragment))
				.toList();
		return Stream.of(treeNodesOfSnapshot, relevantTreeNodesOfDefaultView).flatMap(List::stream)
				.toList();
	}
}
