package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.TimeBasedConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.model.FragmentationTimestamp;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.TimeBasedConstants.DATETIME_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.TimeBasedConstants.TREE_INBETWEEN_RELATION;

public class TimeBasedRelationsAttributer {

    private final FragmentRepository fragmentRepository;

    private final TimeBasedConfig config;

    public TimeBasedRelationsAttributer(FragmentRepository fragmentRepository,
                                        TimeBasedConfig config) {
        this.fragmentRepository = fragmentRepository;
        this.config = config;
    }

    public void addInBetweenRelation(Fragment parentFragment, Fragment childFragment) {
        FragmentationTimestamp timestamp = timestampFromFragmentPairs(childFragment);
        TreeRelation parentChildRelation = new TreeRelation(config.getFragmentationPath(),
                childFragment.getFragmentId(),
                timestamp.asString(), DATETIME_TYPE,
                TREE_INBETWEEN_RELATION);
        if (!parentFragment.containsRelation(parentChildRelation)) {
            parentFragment.addRelation(parentChildRelation);
            fragmentRepository.saveFragment(parentFragment);
        }
    }

    private FragmentationTimestamp timestampFromFragmentPairs(Fragment fragment) {

        Map<String, String> timeMap = new HashMap<>();
        fragment.getFragmentPairs().stream().filter(fragmentPair -> Arrays.stream(TimeBasedConstants.temporalFields).anyMatch(t -> t.equals(fragmentPair.fragmentKey())))
                .forEach(pair -> timeMap.put(pair.fragmentKey(), pair.fragmentValue()));
        return new FragmentationTimestamp(timeMap);
    }
}
