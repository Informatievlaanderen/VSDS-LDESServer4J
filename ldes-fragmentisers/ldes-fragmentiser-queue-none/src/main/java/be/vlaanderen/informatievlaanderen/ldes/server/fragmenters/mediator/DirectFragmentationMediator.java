package be.vlaanderen.informatievlaanderen.ldes.server.fragmenters.mediator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectFragmentationMediator implements FragmentationMediator {
	private static final Logger LOGGER = LoggerFactory.getLogger(FragmentationMediator.class);
	private final FragmentationExecutor fragmentationExecutor;

	public DirectFragmentationMediator(FragmentationExecutor fragmentationExecutor) {
		LOGGER.info("Server has been configured to NOT use a queue ldes members for fragmentation");
		this.fragmentationExecutor = fragmentationExecutor;
	}

	@Override
	public void addMemberToFragment(String ldesMember) {
		this.processMember(ldesMember);
	}

	@Override
	public void processMember(String ldesMember) {
		fragmentationExecutor.executeFragmentation(ldesMember);
	}
}
