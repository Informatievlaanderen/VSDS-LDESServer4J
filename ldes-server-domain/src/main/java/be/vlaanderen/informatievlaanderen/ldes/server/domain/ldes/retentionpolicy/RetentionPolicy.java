package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

public interface RetentionPolicy {

	/**
	 * When a Member matches the RetentionPolicy it's a candidate for removal from a
	 * View and eventually from the MemberRepository
	 *
	 * @param member
	 *            provided Member
	 * @return true when the Member matches the RetentionPolicy
	 */
	boolean matchesPolicy(Member member);
}
