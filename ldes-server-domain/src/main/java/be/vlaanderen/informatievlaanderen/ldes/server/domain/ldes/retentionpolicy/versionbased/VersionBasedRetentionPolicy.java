package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.versionbased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The VersionBasedRetentionPolicy has two arguments numberOfMembersToKeep and a
 * memberRepository.
 * <p>
 * When testing a Member, it first verifies that both the timestamp and
 * versionOf attribute are set on the member.
 * If not, it returns false.
 * Otherwise, it will retrieve all members that have the same versionOf
 * attribute.
 * It will sort these members according to the timestamp attribute.
 * It returns false for the N (numberOfMembersToKeep) most recent members and
 * true for the other members.
 */
public class VersionBasedRetentionPolicy implements RetentionPolicy {
	private final int numberOfMembersToKeep;
	private final MemberRepository memberRepository;

	public VersionBasedRetentionPolicy(int numberOfMembersToKeep, MemberRepository memberRepository) {
		this.numberOfMembersToKeep = numberOfMembersToKeep;
		this.memberRepository = memberRepository;
	}

	@Override
	public boolean matchesPolicy(Member member) {
		String versionOf = member.getVersionOf();
		LocalDateTime timestamp = member.getTimestamp();
		if (versionOf != null && timestamp != null) {
			List<Member> membersOfVersion = memberRepository.getMembersOfVersion(versionOf);
			if (numberOfMembersToKeep >= membersOfVersion.size()) {
				return false;
			}
			List<Member> sortedMembersByTimestampDescending = membersOfVersion.stream()
					.sorted(new ReverseTimeStampComparator())
					.toList();
			List<Member> membersToKeep = sortedMembersByTimestampDescending.subList(0, numberOfMembersToKeep);
			return !membersToKeep.contains(member);
		}
		return false;
	}
}
