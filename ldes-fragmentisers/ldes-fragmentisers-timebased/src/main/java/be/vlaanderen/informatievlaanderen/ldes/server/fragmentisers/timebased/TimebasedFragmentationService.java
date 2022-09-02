package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationServiceDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;

import java.util.Optional;

public class TimebasedFragmentationService extends FragmentationServiceDecorator {

	protected final FragmentCreator fragmentCreator;
	protected final LdesFragmentRepository ldesFragmentRepository;

	public TimebasedFragmentationService(FragmentationService fragmentationService,
			FragmentCreator fragmentCreator,
			LdesFragmentRepository ldesFragmentRepository) {
		super(fragmentationService, ldesFragmentRepository);
		this.fragmentCreator = fragmentCreator;
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, String ldesMemberId) {
		LdesFragment ldesFragment = retrieveLastFragmentOrCreateNewFragment(parentFragment.getFragmentInfo());
		if (!ldesFragment.getMemberIds().contains(ldesMemberId)) {
			ldesFragmentRepository.saveFragment(ldesFragment);
			super.addRelationFromParentToChild(parentFragment, ldesFragment);
			super.addMemberToFragment(ldesFragment, ldesMemberId);
		}
	}

	private LdesFragment retrieveLastFragmentOrCreateNewFragment(FragmentInfo fragmentInfo) {
		return ldesFragmentRepository
				.retrieveChildFragment(fragmentInfo.getCollectionName(), fragmentInfo.getViewName(),
						fragmentInfo.getFragmentPairs())
				.map(fragment -> {
					if (fragmentCreator.needsToCreateNewFragment(fragment)) {
						return fragmentCreator.createNewFragment(Optional.of(fragment), fragmentInfo);
					} else {
						return fragment;
					}
				})
				.orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(), fragmentInfo));
	}
}
