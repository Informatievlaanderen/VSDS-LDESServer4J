package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;

public interface FragmentationMediator {

	void addMemberToFragment(LdesMember ldesMember);

	void processMember(LdesMember ldesMember);
}
