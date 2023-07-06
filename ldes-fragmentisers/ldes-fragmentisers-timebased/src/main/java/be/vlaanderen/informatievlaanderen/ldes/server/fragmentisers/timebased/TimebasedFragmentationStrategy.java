package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.OpenFragmentProvider;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.rdf.model.Model;

public class TimebasedFragmentationStrategy extends FragmentationStrategyDecorator {
	public static final String TIMEBASED_FRAGMENTATION = "TimebasedFragmentation";
	private final OpenFragmentProvider openFragmentProvider;

	private final ObservationRegistry observationRegistry;

	public TimebasedFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			OpenFragmentProvider openFragmentProvider, ObservationRegistry observationRegistry,
			FragmentRepository fragmentRepository) {
		super(fragmentationStrategy, fragmentRepository);
		this.openFragmentProvider = openFragmentProvider;
		this.observationRegistry = observationRegistry;
	}

	@Override public void addMemberToFragment(LdesFragment parentFragment, String memberId, Model memberModel,
			Observation parentObservation) {
		Observation timebasedFragmentationObservation = Observation.createNotStarted("timebased fragmentation",
						observationRegistry)
				.parentObservation(parentObservation)
				.start();
		Pair<LdesFragment, Boolean> ldesFragment = openFragmentProvider
				.retrieveOpenFragmentOrCreateNewFragment(parentFragment);
		if (Boolean.TRUE.equals(ldesFragment.getRight())) {
			super.addRelationFromParentToChild(parentFragment, ldesFragment.getLeft());
		}
		super.addMemberToFragment(ldesFragment.getLeft(), memberId, memberModel, timebasedFragmentationObservation);
		timebasedFragmentationObservation.stop();
	}
}
