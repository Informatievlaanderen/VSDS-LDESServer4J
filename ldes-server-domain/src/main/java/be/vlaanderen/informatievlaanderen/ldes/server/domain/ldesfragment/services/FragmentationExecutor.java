package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

public interface FragmentationExecutor {

	void executeFragmentation(Member memberId);
}
