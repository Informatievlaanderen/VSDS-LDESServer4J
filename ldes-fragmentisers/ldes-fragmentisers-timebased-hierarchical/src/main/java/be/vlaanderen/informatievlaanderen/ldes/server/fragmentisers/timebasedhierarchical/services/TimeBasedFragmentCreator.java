package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
					LOGGER.debug("Timebased fragment created with id: {}", child.getFragmentId());
					return child;
				});
	}
}
