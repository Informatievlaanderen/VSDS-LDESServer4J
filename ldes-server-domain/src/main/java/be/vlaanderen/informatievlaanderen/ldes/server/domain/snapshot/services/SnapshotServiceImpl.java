package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.exception.SnapshotCreationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository.SnapshotRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import org.springframework.stereotype.Component;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig.DEFAULT_VIEW_NAME;

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
		final String viewName = ldesConfig.getCollectionName() + "/" + DEFAULT_VIEW_NAME;
		List<LdesFragment> treeNodesForSnapshot = ldesFragmentRepository.retrieveFragmentsOfView(viewName);
		if (treeNodesForSnapshot.isEmpty()) {
			throw new SnapshotCreationException(
					"No TreeNodes available in view " + DEFAULT_VIEW_NAME + " which is used for snapshotting");
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

}
