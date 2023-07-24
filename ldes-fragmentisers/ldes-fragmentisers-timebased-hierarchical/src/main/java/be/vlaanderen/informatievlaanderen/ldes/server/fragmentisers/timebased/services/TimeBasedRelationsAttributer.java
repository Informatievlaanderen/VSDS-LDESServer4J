package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.TimeBasedConfig;

public class TimeBasedRelationsAttributer {

    private final FragmentRepository fragmentRepository;

    private final TimeBasedConfig config;

    public TimeBasedRelationsAttributer(FragmentRepository fragmentRepository,
                                        TimeBasedConfig config) {
        this.fragmentRepository = fragmentRepository;
        this.config = config;
    }

    public void addTimebasedRelation(Fragment parentFragment, Fragment childFragment) {
        String substringValue = getSubstringValue(childFragment);
        TreeRelation parentChildRelation = new TreeRelation(substringConfig.getFragmentationPath(),
                childFragment.getFragmentId(),
                substringValue, STRING_TYPE,
                TREE_SUBSTRING_RELATION);
        if (!parentFragment.containsRelation(parentChildRelation)) {
            parentFragment.addRelation(parentChildRelation);
            fragmentRepository.saveFragment(parentFragment);
        }
    }

    private String getTimeValue(Fragment childFragment) {
        return childFragment.getValueOfKey(TIMEUNIT).map(time -> time.replace("\"", ""))
                .orElseThrow(
                        () -> new MissingFragmentValueException(childFragment.getFragmentIdString(), SUBSTRING));

    }
}
