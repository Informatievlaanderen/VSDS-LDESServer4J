package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.util.Optional;
import java.util.stream.Stream;

public interface MemberRepository {

	Member saveMemberOfCollection(Member member);

	boolean memberExists(String id);

	Optional<Member> getMember(String id);

	void deleteMember(String memberId);

	void addMemberReference(String memberId, String fragmentId);

	Stream<Member> getMembersByReference(String treeNodeId);

	void removeMemberReference(String memberId, String fragmentId);
}
