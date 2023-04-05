package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.bucketiser.SubstringBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentFinder;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.util.List;

public class SubstringFragmentationStrategy extends FragmentationStrategyDecorator {

	private final ObservationRegistry observationRegistry;
	private final SubstringBucketiser substringBucketiser;
	private final SubstringFragmentFinder substringFragmentFinder;
	private final SubstringFragmentCreator substringFragmentCreator;

	public SubstringFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			ObservationRegistry observationRegistry,
			SubstringBucketiser substringBucketiser,
			SubstringFragmentFinder substringFragmentFinder, SubstringFragmentCreator substringFragmentCreator,
			TreeRelationsRepository treeRelationsRepository) {
		super(fragmentationStrategy, treeRelationsRepository);
		this.observationRegistry = observationRegistry;
		this.substringBucketiser = substringBucketiser;
		this.substringFragmentFinder = substringFragmentFinder;
		this.substringFragmentCreator = substringFragmentCreator;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Observation parentObservation) {
		Observation substringFragmentationObservation = Observation.createNotStarted("substring fragmentation",
				observationRegistry)
				.parentObservation(parentObservation)
				.start();
		List<String> buckets = substringBucketiser.bucketise(member);
		LdesFragment rootFragment = substringFragmentCreator.getOrCreateSubstringFragment(parentFragment, "");
		super.addRelationFromParentToChild(parentFragment, rootFragment);
		LdesFragment substringFragment = substringFragmentFinder
				.getOpenLdesFragmentOrLastPossibleFragment(parentFragment, rootFragment, buckets);
		super.addMemberToFragment(substringFragment, member, substringFragmentationObservation);
		substringFragmentationObservation.stop();
	}

}
