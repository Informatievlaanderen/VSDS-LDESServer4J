package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ModelParser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.constants.TimeBasedFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.model.FragmentationTimestamp;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedFragmentCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public class TimeBasedFragmentationStrategy extends FragmentationStrategyDecorator {

	public static final String TIMEBASED_FRAGMENTATION = "TimeBasedFragmentation";

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

		final Observation fragmentationObservation = startFragmentationObservation(parentObservation);
		FragmentationTimestamp fragmentationTimestamp = getFragmentationTimestamp(memberModel);
		final Fragment rootFragment = prepareRootFragment(parentFragment);

		Fragment fragment = fragmentFinder.getLowestFragment(parentFragment, fragmentationTimestamp, rootFragment, 0);
		super.addMemberToFragment(fragment, memberId, memberModel, fragmentationObservation);
		fragmentationObservation.stop();
	}

	private FragmentationTimestamp getFragmentationTimestamp(Model memberModel) {
		return new FragmentationTimestamp( (LocalDateTime) ModelParser.getFragmentationObject(memberModel,
				config.getFragmenterSubjectFilter(),
				config.getFragmentationPath()), config.getMaxGranularity());
	}

	private Fragment prepareRootFragment(Fragment parentFragment) {
		final Fragment rootFragment = fragmentCreator.getOrCreateRootFragment(parentFragment);
		super.addRelationFromParentToChild(parentFragment, rootFragment);
		return rootFragment;
	}

	private Observation startFragmentationObservation(Observation parentObservation) {
		return Observation.createNotStarted("timebased fragmentation", observationRegistry)
				.parentObservation(parentObservation)
				.start();
	}

}
