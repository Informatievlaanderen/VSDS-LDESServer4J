package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

public interface FragmentationQueueMediator {

	void addLdesMember(String memberId);

	boolean queueIsEmtpy();
}
