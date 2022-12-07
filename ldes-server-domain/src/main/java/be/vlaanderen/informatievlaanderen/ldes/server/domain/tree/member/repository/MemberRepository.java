package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.util.stream.Stream;

public interface MemberRepository {

	boolean saveLdesMember(Member member);

	Stream<Member> getLdesMembersByFragment(LdesFragment ldesFragment);

	boolean deleteMember(String memberId);

	boolean addMemberReference(String ldesMemberId, String fragmentId);

	boolean removeMemberReference(String memberId, String fragmentId);

	boolean removeMemberReferencesForFragment(LdesFragment ldesFragment);

	long removeMembersWithNoReferences();
}
