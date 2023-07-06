package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.Member;

public interface MemberRepository {
	public Member saveMember(Member member);
}
