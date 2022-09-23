package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationServiceDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
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
			LdesFragmentRepository ldesFragmentRepository, Tracer tracer) {
		super(fragmentationService, ldesFragmentRepository);
		this.fragmentCreator = fragmentCreator;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.tracer = tracer;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, LdesMember ldesMember, Span parentSpan) {
		Span timebasedFragmentationSpan = tracer.nextSpan(parentSpan).name("timebased fragmentation").start();
		LdesFragment ldesFragment = retrieveLastFragmentOrCreateNewFragment(parentFragment.getFragmentInfo());

		if (!ldesFragment.getMemberIds().contains(ldesMember.getLdesMemberId())) {
			ldesFragmentRepository.saveFragment(ldesFragment);

			if (parentFragment.getRelations().stream()
					.noneMatch(relation -> relation.getRelation().equals(GENERIC_TREE_RELATION))) {
				super.addRelationFromParentToChild(parentFragment, ldesFragment);
			}
			super.addMemberToFragment(ldesFragment, ldesMember, timebasedFragmentationSpan);
		}
		timebasedFragmentationSpan.end();
	}

	private LdesFragment retrieveLastFragmentOrCreateNewFragment(FragmentInfo fragmentInfo) {
		return ldesFragmentRepository
				.retrieveOpenChildFragment(fragmentInfo.getViewName(),
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
