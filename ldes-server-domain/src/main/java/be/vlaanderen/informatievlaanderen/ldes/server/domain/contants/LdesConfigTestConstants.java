package be.vlaanderen.informatievlaanderen.ldes.server.domain.contants;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.FragmentInfo;

import java.util.Map;

public class LdesConfigTestConstants {
    public static final String VIEW_SHORTNAME = "exampleData";
    public static final String VIEW = "http://localhost:8089/exampleData";
    public static final String SHAPE = "http://localhost:8089/exampleData/shape";
    public static final String FRAGMENTATION_TYPE = "https://w3id.org/ldes#timestampPath";
    public static final String PATH = "http://www.w3.org/ns/prov#generatedAtTime";
    public static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";

    public static final String FRAGMENT_ID_1 = VIEW + "?generatedAtTime=" + FRAGMENTATION_VALUE_1;
    public static final FragmentInfo FRAGMENT_INFO = new FragmentInfo(VIEW, SHAPE, null, VIEW_SHORTNAME, PATH,
            FRAGMENTATION_VALUE_1, 10L);

    public static LdesConfig basicLdesConfig() {
        LdesConfig ldesConfig = new LdesConfig();
        return ldesConfig;
    }


    public static Map<String, String> ldesConfigToMap() {
        return Map.of("viewShortName", VIEW, "shape", SHAPE);
    }

}
