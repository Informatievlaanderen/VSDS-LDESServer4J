package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.constants;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.model.FragmentationTimestamp;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedRelationsAttributer;

import java.util.List;

public class TimeBasedFragmentFinder {

    private final TimeBasedFragmentCreator fragmentCreator;
    private final TimeBasedConfig config;
    private final TimeBasedRelationsAttributer relationsAttributer;

    public TimeBasedFragmentFinder(TimeBasedFragmentCreator fragmentCreator, TimeBasedConfig config,
                                   TimeBasedRelationsAttributer relationsAttributer) {
        this.fragmentCreator = fragmentCreator;
        this.config = config;
        this.relationsAttributer = relationsAttributer;
    }

    public Fragment getLowestFragment(Fragment parentFragment, FragmentationTimestamp fragmentationTimestamp, Fragment rootFragment, int granularity) {
        if(isLowest(parentFragment)) {
            return parentFragment;
        }
        return getLowestFragment(fragmentCreator.getOrCreateFragment(parentFragment, fragmentationTimestamp, rootFragment, granularity), fragmentationTimestamp, rootFragment, granularity + 1);
    }

    private boolean isLowest(Fragment fragment) {
        return fragment.getFragmentPairs().stream().anyMatch(fragmentPair -> fragmentPair.fragmentKey().equals(config.getMaxGranularity()));
    }

}
