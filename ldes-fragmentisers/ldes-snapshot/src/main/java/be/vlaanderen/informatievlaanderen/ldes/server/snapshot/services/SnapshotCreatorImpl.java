package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.RootFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.HOST_NAME_KEY;

@Component
public class SnapshotCreatorImpl implements SnapShotCreator {

	private final String hostName;
	private final MemberCollector memberCollector;
	private final RootFragmentCreator rootFragmentCreator;
	private final SnapshotFragmenter snapshotFragmenter;
	private final ShaclShapeService shaclShapeService;

	public SnapshotCreatorImpl(@Value(HOST_NAME_KEY) String hostName, MemberCollector memberCollector,
			RootFragmentCreator rootFragmentCreator, SnapshotFragmenter snapshotFragmenter,
			ShaclShapeService shaclShapeService) {
		this.hostName = hostName;
		this.memberCollector = memberCollector;
		this.rootFragmentCreator = rootFragmentCreator;
		this.snapshotFragmenter = snapshotFragmenter;
		this.shaclShapeService = shaclShapeService;
	}

	@Override
	public Snapshot createSnapshotForTreeNodes(List<Fragment> treeNodesForSnapshot,
			String collectionName) {
		LocalDateTime snapshotTime = LocalDateTime.now();
		Model shacl = shaclShapeService.retrieveShaclShape(collectionName).getModel();
		Snapshot snapshot = new Snapshot(getSnapshotId(collectionName, snapshotTime), collectionName,
				shacl, snapshotTime, hostName + "/" + collectionName);
		Set<Member> membersOfSnapshot = getMembersOfSnapshot(treeNodesForSnapshot);
		Fragment rootTreeNodeOfSnapshot = rootFragmentCreator
				.createRootFragmentForView(ViewName.fromString(snapshot.getSnapshotId()));
		snapshotFragmenter.fragmentSnapshotMembers(membersOfSnapshot, rootTreeNodeOfSnapshot);
		return snapshot;
	}

	private String getSnapshotId(String collectionName, LocalDateTime snapshotTime) {
		return "%s/snapshot-%s".formatted(collectionName, snapshotTime);
	}

	private Set<Member> getMembersOfSnapshot(List<Fragment> fragments) {
		return memberCollector.getMembersGroupedByVersionOf(fragments)
				.values()
				.stream()
				.map(memberList -> memberList
						.stream()
						.reduce(memberList.get(0), new MemberReduceOperator()))
				.collect(Collectors.toSet());
	}
}
