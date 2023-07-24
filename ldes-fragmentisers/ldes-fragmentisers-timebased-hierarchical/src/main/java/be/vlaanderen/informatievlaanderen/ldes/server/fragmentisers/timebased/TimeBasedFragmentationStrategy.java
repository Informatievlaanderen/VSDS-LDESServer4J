package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ModelParser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.constants.TimeBasedFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedFragmentCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class TimeBasedFragmentationStrategy extends FragmentationStrategyDecorator {

	public static final String TIMEBASED_FRAGMENTATION = "TimeBasedFragmentation";

	public static final String ROOT_SUBSTRING = "";

	private final ObservationRegistry observationRegistry;
	private final TimeBasedFragmentFinder fragmentFinder;
	private final TimeBasedFragmentCreator fragmentCreator;
	private final TimeBasedConfig config;

	public TimeBasedFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
										  ObservationRegistry observationRegistry,
										  TimeBasedFragmentFinder fragmentFinder,
										  TimeBasedFragmentCreator fragmentCreator,
										  FragmentRepository fragmentRepository,
										  TimeBasedConfig config) {
		super(fragmentationStrategy, fragmentRepository);
		this.observationRegistry = observationRegistry;
		this.fragmentFinder = fragmentFinder;
		this.fragmentCreator = fragmentCreator;
		this.config = config;
	}

	@Override
	public void addMemberToFragment(Fragment parentFragment, String memberId, Model memberModel,
			Observation parentObservation) {

		final Observation substringFragmentationObservation = startFragmentationObservation(parentObservation);
		LocalDateTime fragmentationTimestamp = getFragmentationTimestamp(memberModel);
		final Fragment rootFragment = prepareRootFragment(parentFragment);

		Set<Fragment> fragments = getSubstringFragments(parentFragment, fragmentationTimestamp, rootFragment);
		fragments.parallelStream()
				.forEach(substringFragment -> super.addMemberToFragment(substringFragment, memberId, memberModel,
						substringFragmentationObservation));
		substringFragmentationObservation.stop();
	}

	private LocalDateTime getFragmentationTimestamp(Model memberModel) {
		return (LocalDateTime) ModelParser.getFragmentationObject(memberModel,
				config.getFragmenterSubjectFilter(),
				config.getFragmentationPath());
	}

	private Fragment prepareRootFragment(Fragment parentFragment) {
		final Fragment rootFragment = fragmentCreator.getOrCreateRootFragment(parentFragment);
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
		return Observation.createNotStarted("timebased fragmentation", observationRegistry)
				.parentObservation(parentObservation)
				.start();
	}

}
