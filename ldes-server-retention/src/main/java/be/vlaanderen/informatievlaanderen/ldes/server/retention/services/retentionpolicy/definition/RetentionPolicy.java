package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;

public interface RetentionPolicy {

	/**
	 * When a MemberProperties matches the RetentionPolicy it's a candidate for
	 * removal from a
	 * View and eventually from the Collection
	 *
	 * @param memberProperties
	 *            provided MemberProperties
	 * @return true when the MemberProperties matches the RetentionPolicy
	 */
	boolean matchesPolicy(MemberProperties memberProperties);
}
