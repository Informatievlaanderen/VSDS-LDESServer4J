package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.constants;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.FragmentInfo;

import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.LdesConfigTestConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.LdesConfigTestConstants.VIEW;

public class MongoTestConstants {

    public static String FRAGMENT_VALUE_1 = "2022-03-03T00:00:00.000Z";
    public static String FRAGMENT_VALUE_2 = "2022-03-04T00:00:00.000Z";
    public static String FRAGMENT_VALUE_3 = "2022-03-05T00:00:00.000Z";

    public static Long MEMBER_LIMIT = 3L;

    public static FragmentInfo fragmentInfo(String fragmentValue) {
        return new FragmentInfo(VIEW, SHAPE, null, VIEW_SHORTNAME, PATH, fragmentValue, MEMBER_LIMIT);
    }
}
