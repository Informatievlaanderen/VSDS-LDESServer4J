package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.LDES_SERVER_CREATE_FRAGMENTS_COUNT;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.FRAGMENTATION_STRATEGY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.VIEW;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.HierarchicalTimeBasedFragmentationStrategy.TIMEBASED_FRAGMENTATION_HIERARCHICAL;

public class TimeBasedFragmentCreator {

	private final FragmentRepository fragmentRepository;
	private final TimeBasedRelationsAttributer relationsAttributer;
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeBasedFragmentCreator.class);

	public TimeBasedFragmentCreator(FragmentRepository fragmentRepository,
			TimeBasedRelationsAttributer relationsAttributer) {
		this.fragmentRepository = fragmentRepository;
		this.relationsAttributer = relationsAttributer;
	}

	public Fragment getOrCreateFragment(Fragment parentFragment,
										FragmentationTimestamp fragmentationTimestamp,
										Granularity granularity) {
		return getOrCreateFragment(parentFragment, fragmentationTimestamp.getTimeValueForGranularity(granularity), granularity);
	}

	public Fragment getOrCreateFragment(Fragment parentFragment,
										String timeValue,
										Granularity granularity) {

		// granu

		Fragment child = parentFragment.createChild(new FragmentPair(granularity.getValue(), timeValue));
		return fragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					// TODO TVB: moet afhankelijk zijn van configuratie
					// TODO TVB: bij het maken van een nieuw fragment, dan kan/ZAL er heel snel een child komen
					// TODO TVB: bij het toevoegen van een relatie moet dit herberekend worden
					child.setNextUpdateTs(granularity.getFoo());
					fragmentRepository.saveFragment(child);
					addRelationToParent(parentFragment, child);
					logFragmentation(parentFragment, child);
					return child;
				});
	}

	private void addRelationToParent(Fragment parentFragment, Fragment child) {
		if (isDefaultBucket(child)) {
			relationsAttributer.addDefaultRelation(parentFragment, child);
		} else {
			relationsAttributer.addInBetweenRelation(parentFragment, child);
		}
	}

	private boolean isDefaultBucket(Fragment fragment) {
		return fragment.getValueOfKey(Granularity.YEAR.getValue()).orElse("").equals(DEFAULT_BUCKET_STRING);
	}

	private void logFragmentation(Fragment parentFragment, Fragment child) {
		String viewName = parentFragment.getViewName().asString();
		Metrics
				.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT, VIEW, viewName, FRAGMENTATION_STRATEGY, TIMEBASED_FRAGMENTATION_HIERARCHICAL)
				.increment();
		LOGGER.debug("Timebased fragment created with id: {}", child.getFragmentId());
	}

}
