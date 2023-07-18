package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

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
	private final MemberPropertiesRepository memberPropertiesRepository;

	public VersionBasedRetentionPolicy(int numberOfMembersToKeep,
			MemberPropertiesRepository memberPropertiesRepository) {
		this.numberOfMembersToKeep = numberOfMembersToKeep;
		this.memberPropertiesRepository = memberPropertiesRepository;
	}

	@Override
	public boolean matchesPolicyOfView(MemberProperties memberProperties, String viewName) {
		String versionOf = memberProperties.getVersionOf();
		LocalDateTime timestamp = memberProperties.getTimestamp();
		if (versionOf != null && timestamp != null) {
			List<MemberProperties> membersOfVersion = memberPropertiesRepository
					.getMemberPropertiesOfVersionAndView(versionOf, viewName);
			if (numberOfMembersToKeep >= membersOfVersion.size()) {
				return false;
			}
			List<MemberProperties> sortedMembersByTimestampDescending = membersOfVersion.stream()
					.sorted(new ReverseTimeStampComparator())
					.toList();
			List<MemberProperties> membersToKeep = sortedMembersByTimestampDescending.subList(0, numberOfMembersToKeep);
			return !membersToKeep.contains(memberProperties);
		}
		return false;
	}
}
