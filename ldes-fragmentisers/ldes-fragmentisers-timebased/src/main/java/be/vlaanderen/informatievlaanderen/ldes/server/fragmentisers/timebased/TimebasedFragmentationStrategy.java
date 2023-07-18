package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.OpenFragmentProvider;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.commons.lang3.tuple.Pair;

public class TimebasedFragmentationStrategy extends FragmentationStrategyDecorator {
	public static final String TIMEBASED_FRAGMENTATION = "TimebasedFragmentation";
	private final OpenFragmentProvider openFragmentProvider;

	private final ObservationRegistry observationRegistry;

	public TimebasedFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			OpenFragmentProvider openFragmentProvider, ObservationRegistry observationRegistry,
			LdesFragmentRepository ldesFragmentRepository) {
		super(fragmentationStrategy, ldesFragmentRepository);
		this.openFragmentProvider = openFragmentProvider;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Observation parentObservation) {
		Observation timebasedFragmentationObservation = Observation.createNotStarted("timebased fragmentation",
				observationRegistry)
				.parentObservation(parentObservation)
				.start();
		Pair<LdesFragment, Boolean> ldesFragment = openFragmentProvider
				.retrieveOpenFragmentOrCreateNewFragment(parentFragment);
		if (Boolean.TRUE.equals(ldesFragment.getRight())) {
			super.addRelationFromParentToChild(parentFragment, ldesFragment.getLeft());
		}
		super.addMemberToFragment(ldesFragment.getLeft(), member, timebasedFragmentationObservation);
		timebasedFragmentationObservation.stop();
	}

}
