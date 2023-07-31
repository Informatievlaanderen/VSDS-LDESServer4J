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

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.ModelParser.getFragmentationObjectLocalDateTime;

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
		FragmentationTimestamp fragmentationTimestamp = getFragmentationTimestamp(memberModel);

		Fragment fragment = fragmentFinder.getLowestFragment(parentFragment, fragmentationTimestamp, Granularity.YEAR);
		super.addMemberToFragment(fragment, memberId, memberModel, fragmentationObservation);
		fragmentationObservation.stop();
	}

	private FragmentationTimestamp getFragmentationTimestamp(Model memberModel) {
		return new FragmentationTimestamp(getFragmentationObjectLocalDateTime(memberModel,
				config.getFragmenterSubjectFilter(),
				config.getFragmentationPath()), config.getMaxGranularity());
	}

	private Observation startFragmentationObservation(Observation parentObservation) {
		return Observation.createNotStarted("timebased fragmentation", observationRegistry)
				.parentObservation(parentObservation)
				.start();
	}

}
