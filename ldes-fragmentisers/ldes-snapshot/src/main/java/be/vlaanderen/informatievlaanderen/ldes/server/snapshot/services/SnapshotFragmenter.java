package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import java.util.Set;

public interface SnapshotFragmenter {
	void fragmentSnapshotMembers(Set<Member> membersOfSnapshot, Fragment rootTreeNodeOfSnapshot);
}
