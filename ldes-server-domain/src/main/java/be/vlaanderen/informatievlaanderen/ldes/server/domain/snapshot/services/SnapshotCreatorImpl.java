package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.RootFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SnapshotCreatorImpl implements SnapShotCreator {
	private final LdesConfig ldesConfig;
	private final MemberCollector memberCollector;
	private final RootFragmentCreator rootFragmentCreator;
	private final SnapshotFragmenter snapshotFragmenter;

	public SnapshotCreatorImpl(LdesConfig ldesConfig, MemberCollector memberCollector,
			RootFragmentCreator rootFragmentCreator, SnapshotFragmenter snapshotFragmenter) {
		this.ldesConfig = ldesConfig;
		this.memberCollector = memberCollector;
		this.rootFragmentCreator = rootFragmentCreator;
		this.snapshotFragmenter = snapshotFragmenter;
	}

	@Override
	public Snapshot createSnapshotForTreeNodes(List<LdesFragment> treeNodesForSnapshot) {
		LocalDateTime snapshotTime = LocalDateTime.now();
		Snapshot snapshot = new Snapshot("snapshot-" + snapshotTime,
				ldesConfig.validation().getShape(), snapshotTime, ldesConfig.getBaseUrl());
		Set<Member> membersOfSnapshot = getMembersOfSnapshot(treeNodesForSnapshot);
		LdesFragment rootTreeNodeOfSnapshot = rootFragmentCreator.createRootFragmentForView(snapshot.getSnapshotId());
		snapshotFragmenter.fragmentSnapshotMembers(membersOfSnapshot, rootTreeNodeOfSnapshot);
		return snapshot;
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
