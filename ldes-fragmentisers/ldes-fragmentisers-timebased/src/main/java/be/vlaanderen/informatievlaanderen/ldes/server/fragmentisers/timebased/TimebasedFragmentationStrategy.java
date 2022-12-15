package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.OpenFragmentProvider;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

public class TimebasedFragmentationStrategy extends FragmentationStrategyDecorator {
	private final OpenFragmentProvider openFragmentProvider;
	private final ObservationRegistry observationRegistry;

	public TimebasedFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			LdesFragmentRepository ldesFragmentRepository, OpenFragmentProvider openFragmentProvider,
			ObservationRegistry observationRegistry) {
		super(fragmentationStrategy, ldesFragmentRepository);
		this.openFragmentProvider = openFragmentProvider;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Observation parentObservation) {
		Observation timebasedFragmentationSpan = Observation.createNotStarted("timebased fragmentation",
						observationRegistry)
				.parentObservation(parentObservation)
				.start();
		LdesFragment ldesFragment = openFragmentProvider
				.retrieveOpenFragmentOrCreateNewFragment(parentFragment);
		if (!ldesFragment.getMemberIds().contains(member.getLdesMemberId())) {
			if (parentFragment.getRelations().stream()
					.noneMatch(relation -> relation.relation().equals(GENERIC_TREE_RELATION))) {
				super.addRelationFromParentToChild(parentFragment, ldesFragment);
			}
			super.addMemberToFragment(ldesFragment, member, timebasedFragmentationSpan);
		}
		timebasedFragmentationSpan.stop();
	}

}
