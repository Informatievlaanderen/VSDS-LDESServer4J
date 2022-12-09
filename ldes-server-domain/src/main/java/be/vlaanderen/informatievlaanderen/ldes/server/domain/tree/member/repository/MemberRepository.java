package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

	Member saveLdesMember(Member member);

	boolean memberExists(String id);

	Optional<Member> getMember(String id);

	void deleteMember(String memberId);

	void addMemberReference(String memberId, String fragmentId);

	List<Member> getMembersByReference(String treeNodeId);

	void removeMemberReference(String memberId, String fragmentId);
}
