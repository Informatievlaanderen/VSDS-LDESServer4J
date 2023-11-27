package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicyType;

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
public record VersionBasedRetentionPolicy(int numberOfMembersToKeep) implements RetentionPolicy {

	@Override
	public RetentionPolicyType getType() {
		return RetentionPolicyType.VERSION_BASED;
	}

}
