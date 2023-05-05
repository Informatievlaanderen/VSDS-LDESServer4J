package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @deprecated will be replaced in separate modules such as
 *             ldes-server-port-ingest
 */
@Deprecated(since = "start of serverV2")
public interface MemberRepository {

	Member saveLdesMember(Member member);

	boolean memberExists(String id);

	Optional<Member> getMember(String id);

	void deleteMember(String memberId);

	void addMemberReference(String memberId, String fragmentId);

	Stream<Member> getMembersByReference(String treeNodeId);

	void removeMemberReference(String memberId, String fragmentId);

	Stream<Member> getMemberStreamOfCollection(String collectionName);
}
