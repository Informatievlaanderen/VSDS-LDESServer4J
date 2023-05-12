package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

public interface RetentionPolicy {

	boolean matchesPolicy(Member member);
}
