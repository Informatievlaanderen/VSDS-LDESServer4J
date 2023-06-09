package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model.FragmentationString;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.util.Set;
import java.util.stream.Collectors;

public class SubstringFragmentationStrategy extends FragmentationStrategyDecorator {

	public static final String ROOT_SUBSTRING = "";

	private final ObservationRegistry observationRegistry;
	private final SubstringFragmentFinder substringFragmentFinder;
	private final SubstringFragmentCreator substringFragmentCreator;
	private final SubstringConfig substringConfig;

	public SubstringFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			ObservationRegistry observationRegistry,
			SubstringFragmentFinder substringFragmentFinder,
			SubstringFragmentCreator substringFragmentCreator,
			LdesFragmentRepository ldesFragmentRepository,
			SubstringConfig substringConfig) {
		super(fragmentationStrategy, ldesFragmentRepository);
		this.observationRegistry = observationRegistry;
		this.substringFragmentFinder = substringFragmentFinder;
		this.substringFragmentCreator = substringFragmentCreator;
		this.substringConfig = substringConfig;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Observation parentObservation) {

		final Observation substringFragmentationObservation = startFragmentationObservation(parentObservation);
		FragmentationString fragmentationString = new FragmentationString(getFragmentationString(member));
		final LdesFragment rootFragment = prepareRootFragment(parentFragment);
		Set<LdesFragment> ldesFragments = getSubstringFragments(parentFragment, fragmentationString, rootFragment);
		ldesFragments.parallelStream()
				.forEach(substringFragment -> super.addMemberToFragment(substringFragment, member,
						substringFragmentationObservation));
		substringFragmentationObservation.stop();
	}

	private String getFragmentationString(Member member) {
		return (String) member.getFragmentationObject(substringConfig.getFragmenterSubjectFilter(),
				substringConfig.getFragmenterPath());
	}

	private LdesFragment prepareRootFragment(LdesFragment parentFragment) {
		final LdesFragment rootFragment = substringFragmentCreator.getOrCreateSubstringFragment(parentFragment,
				ROOT_SUBSTRING);
		super.addRelationFromParentToChild(parentFragment, rootFragment);
		return rootFragment;
	}

	private Set<LdesFragment> getSubstringFragments(LdesFragment parentFragment,
			FragmentationString fragmentationString,
			LdesFragment rootFragment) {
		return fragmentationString
				.getTokens()
				.stream()
				.map(token -> substringFragmentFinder
						.getOpenOrLastPossibleFragment(parentFragment, rootFragment, token.getBuckets()))
				.collect(Collectors.toSet());

	}

	private Observation startFragmentationObservation(Observation parentObservation) {
		return Observation.createNotStarted("substring fragmentation", observationRegistry)
				.parentObservation(parentObservation)
				.start();
	}

}
