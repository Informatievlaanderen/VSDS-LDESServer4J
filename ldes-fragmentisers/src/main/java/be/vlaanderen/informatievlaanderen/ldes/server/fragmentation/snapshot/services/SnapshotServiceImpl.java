package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository.SnapshotRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.snapshot.exception.SnapshotCreationException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewServiceImpl.DEFAULT_VIEW_NAME;

@Component
public class SnapshotServiceImpl implements SnapshotService {
	private final SnapShotCreator snapShotCreator;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final SnapshotRepository snapshotRepository;
	private final SnapshotRelationLinker snapshotRelationLinker;

	public SnapshotServiceImpl(SnapShotCreator snapShotCreator, LdesFragmentRepository ldesFragmentRepository,
			SnapshotRepository snapshotRepository, SnapshotRelationLinker snapshotRelationLinker) {
		this.snapShotCreator = snapShotCreator;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.snapshotRepository = snapshotRepository;
		this.snapshotRelationLinker = snapshotRelationLinker;
	}

	@Override
	public void createSnapshot(String collectionName) {
		Optional<Snapshot> lastSnapshot = retrieveLastSnapshot();

		ViewName viewName = new ViewName(collectionName, DEFAULT_VIEW_NAME);

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
		createSnapshotForTreeNodes(treeNodesForSnapshot, collectionName);
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
			String collectionName) {
		Snapshot snapshot = snapShotCreator.createSnapshotForTreeNodes(treeNodesForSnapshot, collectionName);
		LdesFragment lastTreeNodeOfSnapshot = snapshotRelationLinker.addRelationsToUncoveredTreeNodes(snapshot,
				treeNodesForSnapshot);
		ldesFragmentRepository.saveFragment(lastTreeNodeOfSnapshot);
		snapshotRepository.saveSnapShot(snapshot);
	}

	private Optional<Snapshot> retrieveLastSnapshot() {
		return snapshotRepository.getLastSnapshot();
	}

	private Optional<LdesFragmentIdentifier> getNextFragmentFromSnapshot(List<LdesFragment> treeNodesOfSnapshot) {
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
		LdesFragmentIdentifier lastFragment = getNextFragmentFromSnapshot(treeNodesOfSnapshot)
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
