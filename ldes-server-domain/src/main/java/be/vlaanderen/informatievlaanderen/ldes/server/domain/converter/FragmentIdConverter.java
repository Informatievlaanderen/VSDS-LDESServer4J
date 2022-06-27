package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FragmentIdConverter {
    private static final Pattern pattern = Pattern.compile("^([^\\?]*)(?:\\?(.*)=(.*))?");
    private final String node;

    public FragmentIdConverter(final String node) {
        this.node = node;
    }

    public String getNode() {
        return node;
    }

    public static String getViewFromFragmentId(String fragmentId) {
        return getPatternGroup(fragmentId, 1);
    }

    public static String getPathFromFragmentId(String fragmentId) {
        return getPatternGroup(fragmentId, 2);
    }

    public static String getValueFromFragmentId(String fragmentId) {
        return getPatternGroup(fragmentId, 3);
    }

    public static String toFragmentId(String hostLocation, String view, String fragmentationType,
            String fragmentationValue) {
        return "%s/%s?%s=%s".formatted(hostLocation, view, getCompactTimestampPath(fragmentationType), fragmentationValue);
    }

    public static String getPatternGroup(String fragmentId, int i) {
        Matcher matcher = pattern.matcher(fragmentId);
        if (matcher.matches()) {
            return matcher.toMatchResult().group(i);
        } else {
            return null;
        }
    }

    private static String getCompactTimestampPath(String timestampPath){
        return timestampPath.split("#")[1];
    }
}
