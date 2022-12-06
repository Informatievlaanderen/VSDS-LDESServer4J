package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

public interface TreeMemberRemover {
	boolean tryRemovingMember(String memberId);
}
