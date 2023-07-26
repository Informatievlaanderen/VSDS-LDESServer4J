package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities.Member;

import java.util.Set;

public interface SnapshotFragmenter {
	void fragmentSnapshotMembers(Set<Member> membersOfSnapshot, Fragment rootTreeNodeOfSnapshot);
}
