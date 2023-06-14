package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.view;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;

import java.util.Map;

public class FragmentationRenamer {

    private static final Map<String, String> newFragmentationNameMap = Map.of(
            "pagination", "PaginationFragmentation",
            "geospatial", "GeospatialFragmentation",
            "substring", "SubstringFragmentation",
            "timebased", "TimebasedFragmentation"
    );

    public static FragmentationConfig rename(FragmentationConfig fragmentationConfig) {
        String fragmentationName = fragmentationConfig.getName();
        fragmentationConfig.setName(newFragmentationNameMap.getOrDefault(fragmentationName, fragmentationName));
        return fragmentationConfig;
    }

}
