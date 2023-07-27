package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities.Member;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.BinaryOperator;

@SuppressWarnings("java:S125")
public class MemberReduceOperator implements BinaryOperator<Member> {
	@Override
	public Member apply(Member member, Member member2) {

		throw new NotImplementedException("To be implemented with snapshot member class");
		// if (member.getTimestamp().isAfter(member2.getTimestamp())) {
		// return member;
		// }
		// return member2;

	}
}
