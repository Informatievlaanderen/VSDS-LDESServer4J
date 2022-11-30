package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.memberreferences.entities;

public interface MemberReferencesRepository {

	void saveMemberReference(final String memberId, final String treeNodeId);

	void removeMemberReference(final String memberId, final String treeNodeId);

	boolean hasMemberReferences(final String memberId);

	void deleteMemberReference(String memberId);

	void addMemberReference(String ldesMemberId, String fragmentId);
}
