package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.RootFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SnapshotCreatorImpl implements SnapShotCreator {

	private final AppConfig appConfig;
	private final MemberCollector memberCollector;
	private final RootFragmentCreator rootFragmentCreator;
	private final SnapshotFragmenter snapshotFragmenter;

	public SnapshotCreatorImpl(AppConfig appConfig, MemberCollector memberCollector,
			RootFragmentCreator rootFragmentCreator, SnapshotFragmenter snapshotFragmenter) {
		this.appConfig = appConfig;
		this.memberCollector = memberCollector;
		this.rootFragmentCreator = rootFragmentCreator;
		this.snapshotFragmenter = snapshotFragmenter;
	}

	@Override
	public Snapshot createSnapshotForTreeNodes(List<LdesFragment> treeNodesForSnapshot,
			EventStream eventStream, ShaclShape shape) {
		LocalDateTime snapshotTime = LocalDateTime.now();
		String collectionName = eventStream.getCollection();
		Snapshot snapshot = new Snapshot(getSnapshotId(collectionName, snapshotTime), collectionName,
				shape.getModel(), snapshotTime, appConfig.getHostName() + "/" + collectionName);
		Set<Member> membersOfSnapshot = getMembersOfSnapshot(treeNodesForSnapshot);
		LdesFragment rootTreeNodeOfSnapshot = rootFragmentCreator
				.createRootFragmentForView(ViewName.fromString(snapshot.getSnapshotId()));
		snapshotFragmenter.fragmentSnapshotMembers(membersOfSnapshot, rootTreeNodeOfSnapshot);
		return snapshot;
	}

	private String getSnapshotId(String collectionName, LocalDateTime snapshotTime) {
		return "%s/snapshot-%s".formatted(collectionName, snapshotTime);
	}

	private Set<Member> getMembersOfSnapshot(List<LdesFragment> ldesFragments) {
		return memberCollector.getMembersGroupedByVersionOf(ldesFragments)
				.values()
				.stream()
				.map(memberList -> memberList
						.stream()
						.reduce(memberList.get(0), new MemberReduceOperator()))
				.collect(Collectors.toSet());
	}
}
