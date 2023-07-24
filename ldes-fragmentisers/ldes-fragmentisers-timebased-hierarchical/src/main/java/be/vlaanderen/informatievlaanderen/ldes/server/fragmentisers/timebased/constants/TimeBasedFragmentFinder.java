package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.constants;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimeBasedConfig;
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

    public Fragment getLowestFragment() {

    }

    public Fragment getOpenOrLastPossibleFragment(Fragment parentFragment,
                                                  Fragment rootFragment, List<String> buckets) {

        Fragment currentParentFragment = rootFragment;
        Fragment currentChildFragment = null;
        for (String bucket : buckets) {
            if (canBeAddedToRoot(rootFragment, bucket)) {
                return rootFragment;
            }
            if (ROOT_SUBSTRING.equals(bucket)) {
                continue;
            }

            currentChildFragment = substringFragmentCreator.getOrCreateSubstringFragment(parentFragment, bucket);
            substringRelationsAttributer.addSubstringRelation(currentParentFragment, currentChildFragment);
            if (currentChildFragment.getNumberOfMembers() < substringConfig.getMemberLimit()) {
                break;
            }
            currentParentFragment = currentChildFragment;
        }
        return currentChildFragment;
    }

    private boolean canBeAddedToRoot(Fragment rootFragment, String bucket) {
        return ROOT_SUBSTRING.equals(bucket)
                && rootFragment.getNumberOfMembers() < substringConfig.getMemberLimit();
    }
}
