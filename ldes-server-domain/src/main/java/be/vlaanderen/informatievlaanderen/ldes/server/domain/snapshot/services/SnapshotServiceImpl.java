package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.exception.SnapshotCreationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository.SnapshotRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
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

	public SnapshotServiceImpl(SnapShotCreator snapShotCreator, LdesFragmentRepository ldesFragmentRepository,
			SnapshotRepository snapshotRepository,
			SnapshotRelationLinker snapshotRelationLinker) {
		this.snapShotCreator = snapShotCreator;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.snapshotRepository = snapshotRepository;
		this.snapshotRelationLinker = snapshotRelationLinker;
	}

	@Override
	public void createSnapshot(LdesConfig ldesConfig) {
		Optional<Snapshot> lastSnapshot = retrieveLastSnapshot();
		ViewName viewName = ldesConfig.getDefaultView().orElseThrow(() -> new SnapshotCreationException(
				"No default pagination view configured for collection " + ldesConfig.getCollectionName()))
				.getName();
		List<LdesFragment> treeNodesForSnapshot;
		if (lastSnapshot.isPresent()) {
			treeNodesForSnapshot = getTreeNodesForSnapshotFromPreviousSnapshot(viewName, lastSnapshot.get());
		} else {
			treeNodesForSnapshot = ldesFragmentRepository.retrieveFragmentsOfView(viewName.asString());
		}

		if (treeNodesForSnapshot.isEmpty()) {
			throw new SnapshotCreationException(
					"No TreeNodes available in view " + viewName.asString() + " which is used for snapshotting");
		}
		createSnapshotForTreeNodes(treeNodesForSnapshot, ldesConfig);
	}

	private void createSnapshotForTreeNodes(List<LdesFragment> treeNodesForSnapshot, LdesConfig ldesConfig) {
		Snapshot snapshot = snapShotCreator.createSnapshotForTreeNodes(treeNodesForSnapshot, ldesConfig);
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
				.retrieveFragmentsOfView(lastSnapshot.getSnapshotId()).stream()
				.filter(ldesFragment -> !ldesFragment.isRoot()).toList();
		List<LdesFragment> treeNodesOfDefaultView = ldesFragmentRepository.retrieveFragmentsOfView(viewName.asString());
		String lastFragment = getNextFragmentFromSnapshot(treeNodesOfSnapshot)
				.orElseThrow(() -> new SnapshotCreationException(
						"First fragment of " + viewName.asString() + " after previous snapshot "
								+ lastSnapshot.getSnapshotId() + " could not be found"));
		List<LdesFragment> relevantTreeNodesOfDefaultView = treeNodesOfDefaultView.stream()
				.filter(ldesFragment -> !ldesFragment.isRoot()).filter(new GreaterOrEqualsPageFilter(lastFragment))
				.toList();
		return Stream.of(treeNodesOfSnapshot, relevantTreeNodesOfDefaultView).flatMap(List::stream)
				.toList();
	}
}
