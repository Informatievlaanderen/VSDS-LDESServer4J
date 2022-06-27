package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.constants;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.FragmentInfo;

public class MongoTestConstants {

    public static String FRAGMENT_VALUE_1 = "2022-03-03T00:00:00.000Z";
    public static String FRAGMENT_VALUE_2 = "2022-03-04T00:00:00.000Z";
    public static String FRAGMENT_VALUE_3 = "2022-03-05T00:00:00.000Z";

    public static Long MEMBER_LIMIT = 3L;
    public static final String VIEW_SHORTNAME = "exampleData";
    public static final String VIEW = "http://localhost:8089/exampleData";
    public static final String SHAPE = "http://localhost:8089/exampleData/shape";
    public static final String PATH = "http://www.w3.org/ns/prov#generatedAtTime";

    public static FragmentInfo fragmentInfo(String fragmentValue) {
        return new FragmentInfo(VIEW, SHAPE, null, VIEW_SHORTNAME, PATH, fragmentValue, MEMBER_LIMIT);
    }
}
