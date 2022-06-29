package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

public class FragmentIdConverter {

    public static String toFragmentId(String hostLocation, String view, String fragmentationType,
                                      String fragmentationValue) {
        return "%s/%s?%s=%s".formatted(hostLocation, view, getCompactTimestampPath(fragmentationType), fragmentationValue);
    }

    private static String getCompactTimestampPath(String timestampPath) {
        return timestampPath.split("#")[1];
    }
}
