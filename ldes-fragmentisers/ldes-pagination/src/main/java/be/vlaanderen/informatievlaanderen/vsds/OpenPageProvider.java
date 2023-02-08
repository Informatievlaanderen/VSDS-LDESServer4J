package be.vlaanderen.informatievlaanderen.vsds;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;

public class OpenPageProvider {
	private final PageCreator pageCreator;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final Long memberLimit;

	public OpenPageProvider(PageCreator pageCreator,
			LdesFragmentRepository ldesFragmentRepository, Long memberLimit) {
		this.pageCreator = pageCreator;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.memberLimit = memberLimit;
	}

	public LdesFragment retrieveOpenFragmentOrCreateNewFragment(LdesFragment lastFragment) {
		if (needsToCreateNewFragment(lastFragment)) {
			LdesFragment newFragment = pageCreator.createNewFragment(lastFragment);
			ldesFragmentRepository.saveFragment(newFragment);
			return newFragment;
		} else
			return lastFragment;
	}

	public boolean needsToCreateNewFragment(LdesFragment fragment) {
		return fragment.getNumberOfMembers() >= memberLimit;
	}
}
