package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

/**
 * @deprecated will be replaced in ldes-server-port-ingest
 */
@Deprecated(forRemoval = true)
public interface MemberIngestService {

	void addMember(Member member);
}
