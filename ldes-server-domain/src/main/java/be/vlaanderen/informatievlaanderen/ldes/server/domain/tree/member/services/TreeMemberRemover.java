package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

public interface TreeMemberRemover {
	void deletingMemberFromCollection(String memberId, String collectionName);
}
