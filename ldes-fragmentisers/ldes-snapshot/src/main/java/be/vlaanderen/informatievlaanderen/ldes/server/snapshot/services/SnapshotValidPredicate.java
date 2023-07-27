package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.exception.SnapshotCreationException;

import java.util.function.Predicate;

public class SnapshotValidPredicate implements Predicate<Member> {
	@Override
	public boolean test(Member member) {
		if (member.timestamp() != null && member.versionOf() != null) {
			return true;
		}
		throw new SnapshotCreationException(
				"Member " + member.id() + " does not have a valid timestampPath or versionOfPath");
	}
}
