package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.TimeBasedConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.model.FragmentationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class TimeBasedFragmentCreator {

    public static final String STARTTIME = "startTime";
    private final FragmentRepository fragmentRepository;
    private final TimeBasedRelationsAttributer relationsAttributer;
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeBasedFragmentCreator.class);

    public TimeBasedFragmentCreator(FragmentRepository fragmentRepository,
                                    TimeBasedRelationsAttributer relationsAttributer) {
        this.fragmentRepository = fragmentRepository;
        this.relationsAttributer = relationsAttributer;
    }

    public Fragment getOrCreateFragment(Fragment parentFragment, FragmentationTimestamp fragmentationTimestamp,
                                            Fragment rootTileFragment, int granularity) {
        Fragment child = parentFragment.createChild(new FragmentPair(TimeBasedConstants.temporalFields[granularity], fragmentationTimestamp.getTemporalFieldsWithValues().get(TimeBasedConstants.temporalFields[granularity])));
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

    public Fragment getOrCreateRootFragment(Fragment parentFragment) {
        Fragment child = parentFragment.createChild(new FragmentPair(STARTTIME, LocalDateTime.MIN.toString()));
        return fragmentRepository
                .retrieveFragment(child.getFragmentId())
                .orElseGet(() -> {
                    fragmentRepository.saveFragment(child);
                    LOGGER.debug("Timebased rootfragment created with id: {}", child.getFragmentId());
                    return child;
                });
    }
}
