package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.versionbased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.util.Comparator;

public class ReverseTimeStampComparator implements Comparator<Member> {
	@Override
	public int compare(Member o1, Member o2) {
		return o2.getTimestamp().compareTo(o1.getTimestamp());
	}
}
