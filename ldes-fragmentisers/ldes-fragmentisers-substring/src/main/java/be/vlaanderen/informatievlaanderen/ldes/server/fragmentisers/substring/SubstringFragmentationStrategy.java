package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.processor.SubstringPreProcessor;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.SubstringConstants.ROOT_SUBSTRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator.SUBSTRING;

public class SubstringFragmentationStrategy extends FragmentationStrategyDecorator {

	private final ObservationRegistry observationRegistry;
	private final SubstringFragmentFinder substringFragmentFinder;
	private final SubstringFragmentCreator substringFragmentCreator;
	private final SubstringPreProcessor substringPreProcessor;

	public SubstringFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			ObservationRegistry observationRegistry,
			SubstringFragmentFinder substringFragmentFinder,
			SubstringFragmentCreator substringFragmentCreator,
			TreeRelationsRepository treeRelationsRepository,
			SubstringPreProcessor substringPreProcessor) {
		super(fragmentationStrategy, treeRelationsRepository);
		this.observationRegistry = observationRegistry;
		this.substringFragmentFinder = substringFragmentFinder;
		this.substringFragmentCreator = substringFragmentCreator;
		this.substringPreProcessor = substringPreProcessor;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Observation parentObservation) {
		final Observation substringFragmentationObservation = startFragmentationObservation(parentObservation);

		final LdesFragment rootFragment = prepareRootFragment(parentFragment);
		addMemberToFragments(parentFragment, member, substringFragmentationObservation, rootFragment);
		substringFragmentationObservation.stop();
	}

	private LdesFragment prepareRootFragment(LdesFragment parentFragment) {
		final LdesFragment rootFragment = substringFragmentCreator.getOrCreateSubstringFragment(parentFragment,
				ROOT_SUBSTRING);
		super.addRelationFromParentToChild(parentFragment, rootFragment);
		return rootFragment;
	}

	private void addMemberToFragments(LdesFragment parentFragment,
			Member member,
			Observation substringFragmentationObservation,
			LdesFragment rootFragment) {
		final String substringTarget = substringPreProcessor.getSubstringTarget(member);
		final List<String> tokens = substringPreProcessor.tokenize(substringTarget);
		final Set<String> substringsThatContainMember = new HashSet<>();
		tokens.forEach(token -> {
			final List<String> buckets = substringPreProcessor.bucketize(token);
			if (buckets.stream().noneMatch(substringsThatContainMember::contains)) {
				final LdesFragment substringFragment = substringFragmentFinder
						.getOpenOrLastPossibleFragment(parentFragment, rootFragment, buckets);
				substringsThatContainMember.add(extractSubstringFromFragmentId(substringFragment.getFragmentId()));
				super.addMemberToFragment(substringFragment, member, substringFragmentationObservation);
			}
		});
	}

	private String extractSubstringFromFragmentId(String fragmentId) {
		String[] splitFragmentId = fragmentId.split(SUBSTRING + "=");
		return splitFragmentId.length > 1 ? splitFragmentId[1] : ROOT_SUBSTRING;
	}

	private Observation startFragmentationObservation(Observation parentObservation) {
		return Observation.createNotStarted("substring fragmentation", observationRegistry)
				.parentObservation(parentObservation)
				.start();
	}

}
