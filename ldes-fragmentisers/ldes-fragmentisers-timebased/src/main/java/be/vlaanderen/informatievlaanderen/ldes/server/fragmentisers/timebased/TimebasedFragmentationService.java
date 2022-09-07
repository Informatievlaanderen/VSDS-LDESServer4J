package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationServiceDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.List;
import java.util.Optional;

public class TimebasedFragmentationService extends FragmentationServiceDecorator {
	protected final LdesConfig ldesConfig;
	protected final FragmentCreator fragmentCreator;
	protected final LdesFragmentRepository ldesFragmentRepository;

	private final Tracer tracer;

	public TimebasedFragmentationService(FragmentationService fragmentationService, LdesConfig ldesConfig,
			FragmentCreator fragmentCreator,
			LdesFragmentRepository ldesFragmentRepository,
			Tracer tracer) {
		super(fragmentationService, ldesFragmentRepository);
		this.ldesConfig = ldesConfig;
		this.fragmentCreator = fragmentCreator;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.tracer = tracer;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, String ldesMemberId) {
		Span span = this.tracer.nextSpan().name("Timebased fragmentation").start();
		LdesFragment ldesFragment = retrieveLastFragmentOrCreateNewFragment(
				parentFragment.getFragmentInfo().getFragmentPairs());
		span.event("Fragment retrieved/created");
		if (!ldesFragment.getMemberIds().contains(ldesMemberId)) {
			ldesFragmentRepository.saveFragment(ldesFragment);
			span.end();
			super.addRelationFromParentToChild(parentFragment, ldesFragment);
			super.addMemberToFragment(ldesFragment, ldesMemberId);
		} else {
			span.end();
		}
	}

	private LdesFragment retrieveLastFragmentOrCreateNewFragment(List<FragmentPair> fragmentPairList) {
		return ldesFragmentRepository.retrieveChildFragment(ldesConfig.getCollectionName(), fragmentPairList)
				.map(fragment -> {
					if (fragmentCreator.needsToCreateNewFragment(fragment)) {
						return fragmentCreator.createNewFragment(Optional.of(fragment), fragmentPairList);
					} else {
						return fragment;
					}
				})
				.orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(), fragmentPairList));
	}
}
