package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.util.Set;

public interface SnapshotFragmenter {
	void fragmentSnapshotMembers(Set<Member> membersOfSnapshot, LdesFragment rootTreeNodeOfSnapshot);
}
