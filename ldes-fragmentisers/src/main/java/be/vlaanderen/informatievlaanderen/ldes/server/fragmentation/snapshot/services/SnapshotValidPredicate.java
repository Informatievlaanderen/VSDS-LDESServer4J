package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.snapshot.exception.SnapshotCreationException;

import java.util.function.Predicate;

public class SnapshotValidPredicate implements Predicate<Member> {
	@Override
	public boolean test(Member member) {
		if (member.getTimestamp() != null && member.getVersionOf() != null)
			return true;
		throw new SnapshotCreationException(
				"Member " + member.getLdesMemberId() + " does not have a valid timestampPath or versionOfPath");
	}
}
