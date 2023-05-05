package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.collection.EventStreamCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.ShaclCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.exception.SnapshotCreationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository.SnapshotRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
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
	private final EventStreamCollection eventStreamCollection;
	private final ViewCollection viewCollection;
	private final ShaclCollection shaclCollection;

	public SnapshotServiceImpl(SnapShotCreator snapShotCreator, LdesFragmentRepository ldesFragmentRepository,
			SnapshotRepository snapshotRepository, SnapshotRelationLinker snapshotRelationLinker,
			EventStreamCollection eventStreamCollection, ViewCollection viewCollection,
			ShaclCollection shaclCollection) {
		this.snapShotCreator = snapShotCreator;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.snapshotRepository = snapshotRepository;
		this.snapshotRelationLinker = snapshotRelationLinker;
		this.eventStreamCollection = eventStreamCollection;
		this.viewCollection = viewCollection;
		this.shaclCollection = shaclCollection;
	}

	@Override
	public void createSnapshot(String collectionName) {
		Optional<Snapshot> lastSnapshot = retrieveLastSnapshot();

		EventStream eventStream = eventStreamCollection.retrieveEventStream(collectionName)
				.orElseThrow(() -> new MissingEventStreamException(collectionName));

		ViewName viewName = viewCollection.getViewByViewName(new ViewName(collectionName, ViewConfig.DEFAULT_VIEW_NAME))
				.map(ViewSpecification::getName)
				.orElseThrow(() -> new SnapshotCreationException(
						"No default pagination view configured for collection " + collectionName));

		ShaclShape shape = shaclCollection.retrieveShape(collectionName).orElseThrow();

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
		createSnapshotForTreeNodes(treeNodesForSnapshot, eventStream, shape);
	}

	private void createSnapshotForTreeNodes(List<LdesFragment> treeNodesForSnapshot,
			EventStream eventStream, ShaclShape shape) {
		Snapshot snapshot = snapShotCreator.createSnapshotForTreeNodes(treeNodesForSnapshot, eventStream, shape);
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
