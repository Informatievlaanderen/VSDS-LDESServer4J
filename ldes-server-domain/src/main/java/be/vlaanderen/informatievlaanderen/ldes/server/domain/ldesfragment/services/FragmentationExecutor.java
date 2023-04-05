package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;

public interface FragmentationExecutor {

	void executeFragmentation(LdesMember memberId);
}
