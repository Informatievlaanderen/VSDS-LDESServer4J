package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository.SnapshotRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.exception.SnapshotCreationException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Component
public class SnapshotServiceImpl implements SnapshotService {

	public static final String DEFAULT_VIEW_NAME = "by-page";

	private final SnapShotCreator snapShotCreator;
	private final FragmentRepository fragmentRepository;
	private final SnapshotRepository snapshotRepository;
	private final SnapshotRelationLinker snapshotRelationLinker;

	public SnapshotServiceImpl(SnapShotCreator snapShotCreator, FragmentRepository fragmentRepository,
			SnapshotRepository snapshotRepository, SnapshotRelationLinker snapshotRelationLinker) {
		this.snapShotCreator = snapShotCreator;
		this.fragmentRepository = fragmentRepository;
		this.snapshotRepository = snapshotRepository;
		this.snapshotRelationLinker = snapshotRelationLinker;
	}

	@Override
	public void createSnapshot(String collectionName) {
		Optional<Snapshot> lastSnapshot = retrieveLastSnapshot();

		ViewName viewName = new ViewName(collectionName, DEFAULT_VIEW_NAME);

		List<Fragment> treeNodesForSnapshot;
		if (lastSnapshot.isPresent()) {
			treeNodesForSnapshot = getTreeNodesForSnapshotFromPreviousSnapshot(viewName, lastSnapshot.get());
		} else {
			treeNodesForSnapshot = fragmentRepository.retrieveFragmentsOfView(viewName.asString()).toList();
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

	private void createSnapshotForTreeNodes(List<Fragment> treeNodesForSnapshot,
			String collectionName) {
		Snapshot snapshot = snapShotCreator.createSnapshotForTreeNodes(treeNodesForSnapshot, collectionName);
		Fragment lastTreeNodeOfSnapshot = snapshotRelationLinker.addRelationsToUncoveredTreeNodes(snapshot,
				treeNodesForSnapshot);
		fragmentRepository.saveFragment(lastTreeNodeOfSnapshot);
		snapshotRepository.saveSnapShot(snapshot);
	}

	private Optional<Snapshot> retrieveLastSnapshot() {
		return snapshotRepository.getLastSnapshot();
	}

	private Optional<LdesFragmentIdentifier> getNextFragmentFromSnapshot(List<Fragment> treeNodesOfSnapshot) {
		return treeNodesOfSnapshot.stream().filter(ldesFragment -> !ldesFragment.isRoot())
				.filter(ldesFragment -> !ldesFragment.isImmutable())
				.map(Fragment::getRelations)
				.flatMap(List::stream)
				.map(TreeRelation::treeNode)
				.findFirst();
	}

	private List<Fragment> getTreeNodesForSnapshotFromPreviousSnapshot(ViewName viewName, Snapshot lastSnapshot) {
		List<Fragment> treeNodesOfSnapshot = fragmentRepository
				.retrieveFragmentsOfView(lastSnapshot.getSnapshotId())
				.filter(ldesFragment -> !ldesFragment.isRoot()).toList();
		Stream<Fragment> treeNodesOfDefaultView = fragmentRepository
				.retrieveFragmentsOfView(viewName.asString());
		LdesFragmentIdentifier lastFragment = getNextFragmentFromSnapshot(treeNodesOfSnapshot)
				.orElseThrow(() -> new SnapshotCreationException(
						"First fragment of " + viewName.asString() + " after previous snapshot "
								+ lastSnapshot.getSnapshotId() + " could not be found"));
		List<Fragment> relevantTreeNodesOfDefaultView = treeNodesOfDefaultView
				.filter(ldesFragment -> !ldesFragment.isRoot()).filter(new GreaterOrEqualsPageFilter(lastFragment))
				.toList();
		return Stream.of(treeNodesOfSnapshot, relevantTreeNodesOfDefaultView).flatMap(List::stream)
				.toList();
	}
}
