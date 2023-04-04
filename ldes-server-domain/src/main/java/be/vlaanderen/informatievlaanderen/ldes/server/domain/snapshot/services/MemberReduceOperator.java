package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.util.function.BinaryOperator;

public class MemberReduceOperator implements BinaryOperator<Member> {
	@Override
	public Member apply(Member member, Member member2) {

		if (member.getTimestamp().isAfter(member2.getTimestamp()))
			return member;
		return member2;

	}
}
