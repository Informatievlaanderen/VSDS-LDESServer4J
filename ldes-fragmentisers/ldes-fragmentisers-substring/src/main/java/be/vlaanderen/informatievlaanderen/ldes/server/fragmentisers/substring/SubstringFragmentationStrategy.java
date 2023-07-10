package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ModelParser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model.FragmentationString;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;

import java.util.Set;
import java.util.stream.Collectors;

public class SubstringFragmentationStrategy extends FragmentationStrategyDecorator {

	public static final String SUBSTRING_FRAGMENTATION = "SubstringFragmentation";

	public static final String ROOT_SUBSTRING = "";

	private final ObservationRegistry observationRegistry;
	private final SubstringFragmentFinder substringFragmentFinder;
	private final SubstringFragmentCreator substringFragmentCreator;
	private final SubstringConfig substringConfig;

	public SubstringFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			ObservationRegistry observationRegistry,
			SubstringFragmentFinder substringFragmentFinder,
			SubstringFragmentCreator substringFragmentCreator,
			FragmentRepository fragmentRepository,
			SubstringConfig substringConfig) {
		super(fragmentationStrategy, fragmentRepository);
		this.observationRegistry = observationRegistry;
		this.substringFragmentFinder = substringFragmentFinder;
		this.substringFragmentCreator = substringFragmentCreator;
		this.substringConfig = substringConfig;
	}

	@Override
	public void addMemberToFragment(Fragment parentFragment, String memberId, Model memberModel,
			Observation parentObservation) {

		final Observation substringFragmentationObservation = startFragmentationObservation(parentObservation);
		FragmentationString fragmentationString = new FragmentationString(getFragmentationString(memberModel));
		final Fragment rootFragment = prepareRootFragment(parentFragment);
		Set<Fragment> fragments = getSubstringFragments(parentFragment, fragmentationString, rootFragment);
		fragments.parallelStream()
				.forEach(substringFragment -> super.addMemberToFragment(substringFragment, memberId, memberModel,
						substringFragmentationObservation));
		substringFragmentationObservation.stop();
	}

	private String getFragmentationString(Model memberModel) {
		return (String) ModelParser.getFragmentationObject(memberModel,
				substringConfig.getFragmenterSubjectFilter(),
				substringConfig.getFragmentationPath());
	}

	private Fragment prepareRootFragment(Fragment parentFragment) {
		final Fragment rootFragment = substringFragmentCreator.getOrCreateSubstringFragment(parentFragment,
				ROOT_SUBSTRING);
		super.addRelationFromParentToChild(parentFragment, rootFragment);
		return rootFragment;
	}

	private Set<Fragment> getSubstringFragments(Fragment parentFragment,
			FragmentationString fragmentationString,
			Fragment rootFragment) {
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
