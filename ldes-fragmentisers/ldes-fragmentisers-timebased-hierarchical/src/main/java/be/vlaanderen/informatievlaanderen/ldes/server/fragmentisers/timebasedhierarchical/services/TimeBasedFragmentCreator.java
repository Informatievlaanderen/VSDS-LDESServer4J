package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.constants.CounterConstants.LDES_SERVER_CREATE_FRAGMENTS_COUNT;

public class TimeBasedFragmentCreator {

	private final FragmentRepository fragmentRepository;
	private final TimeBasedRelationsAttributer relationsAttributer;
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeBasedFragmentCreator.class);

	public TimeBasedFragmentCreator(FragmentRepository fragmentRepository,
			TimeBasedRelationsAttributer relationsAttributer) {
		this.fragmentRepository = fragmentRepository;
		this.relationsAttributer = relationsAttributer;
	}

	public Fragment getOrCreateFragment(Fragment parentFragment, FragmentationTimestamp fragmentationTimestamp,
			Granularity granularity) {
		Fragment child = parentFragment
				.createChild(new FragmentPair(granularity.getValue(),
						fragmentationTimestamp.getTimeValueForGranularity(granularity)));
		return fragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					fragmentRepository.saveFragment(child);
					relationsAttributer
							.addInBetweenRelation(parentFragment, child);
					String viewName = parentFragment.getViewName().asString();
					Metrics.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT, "view", viewName, "fragmentation-strategy", "timebased").increment();
					LOGGER.debug("Timebased fragment created with id: {}", child.getFragmentId());
					return child;
				});
	}
}
