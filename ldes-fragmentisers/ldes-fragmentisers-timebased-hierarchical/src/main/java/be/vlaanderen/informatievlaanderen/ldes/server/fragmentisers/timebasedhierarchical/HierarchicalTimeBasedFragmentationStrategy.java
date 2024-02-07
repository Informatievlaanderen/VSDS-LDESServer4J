package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services.TimeBasedFragmentFinder;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.ModelParser.getFragmentationObjectLocalDateTime;

public class HierarchicalTimeBasedFragmentationStrategy extends FragmentationStrategyDecorator {

	public static final String TIMEBASED_FRAGMENTATION_HIERARCHICAL = "HierarchicalTimeBasedFragmentation";

	private final ObservationRegistry observationRegistry;
	private final TimeBasedFragmentFinder fragmentFinder;
	private final TimeBasedConfig config;

	public HierarchicalTimeBasedFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			ObservationRegistry observationRegistry,
			TimeBasedFragmentFinder fragmentFinder,
			FragmentRepository fragmentRepository,
			TimeBasedConfig config) {
		super(fragmentationStrategy, fragmentRepository);
		this.observationRegistry = observationRegistry;
		this.fragmentFinder = fragmentFinder;
		this.config = config;
	}

	@Override
	public void addMemberToFragment(Fragment parentFragment, String memberId, Model memberModel,
			Observation parentObservation) {
		final Observation fragmentationObservation = startFragmentationObservation(parentObservation);

		Fragment fragment = getFragmentationTimestamp(memberModel)
				.map(timestamp -> fragmentFinder.getLowestFragment(parentFragment, timestamp, Granularity.YEAR))
				.orElseGet(() -> fragmentFinder.getDefaultFragment(parentFragment));

		super.addMemberToFragment(fragment, memberId, memberModel, fragmentationObservation);
		fragmentationObservation.stop();
	}

	private Optional<FragmentationTimestamp> getFragmentationTimestamp(Model memberModel) {
		Optional<LocalDateTime> timeStamp = getFragmentationObjectLocalDateTime(memberModel,
				config.getFragmenterSubjectFilter(),
				config.getFragmentationPath());
		return timeStamp.map(localDateTime -> new FragmentationTimestamp(localDateTime, config.getMaxGranularity()));
	}

	private Observation startFragmentationObservation(Observation parentObservation) {
		return Observation.createNotStarted("timebased fragmentation", observationRegistry)
				.parentObservation(parentObservation)
				.start();
	}

}
