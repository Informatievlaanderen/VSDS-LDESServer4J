package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationServiceDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

public class TimebasedFragmentationService extends FragmentationServiceDecorator {
	protected final FragmentCreator fragmentCreator;
	protected final LdesFragmentRepository ldesFragmentRepository;

	private final Tracer tracer;

	public TimebasedFragmentationService(FragmentationService fragmentationService,
			FragmentCreator fragmentCreator,
			LdesFragmentRepository ldesFragmentRepository,
			Tracer tracer) {
		super(fragmentationService, ldesFragmentRepository);
		this.fragmentCreator = fragmentCreator;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.tracer = tracer;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, String ldesMemberId) {
		Span span = this.tracer.nextSpan().name("Timebased fragmentation").start();
		LdesFragment ldesFragment = retrieveLastFragmentOrCreateNewFragment(parentFragment.getFragmentInfo());
		span.event("Fragment retrieved/created");
		if (!ldesFragment.getMemberIds().contains(ldesMemberId)) {
			ldesFragmentRepository.saveFragment(ldesFragment);
			span.end();
			if(parentFragment.getRelations().stream().noneMatch(relation -> relation.getRelation().equals(GENERIC_TREE_RELATION))){
				super.addRelationFromParentToChild(parentFragment, ldesFragment);
			}
			super.addMemberToFragment(ldesFragment, ldesMemberId);
		} else {
			span.end();
		}
	}

	private LdesFragment retrieveLastFragmentOrCreateNewFragment(FragmentInfo fragmentInfo) {
		return ldesFragmentRepository
				.retrieveChildFragment(fragmentInfo.getViewName(),
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
