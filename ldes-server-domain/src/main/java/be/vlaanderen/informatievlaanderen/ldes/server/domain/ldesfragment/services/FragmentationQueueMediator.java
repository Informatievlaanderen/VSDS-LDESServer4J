package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

public interface FragmentationQueueMediator {

	void addMemberToFragment(String ldesMember);

	void processMember(String ldesMember);
}
