package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.model;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.TimeBasedConstants;
import org.apache.commons.lang3.ArrayUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.TimeBasedConstants.*;

public class FragmentationTimestamp {
    private final Map<String, String> timeMap;
    private final String granularity;

    public FragmentationTimestamp(LocalDateTime time, String granularity) {
        this.timeMap = localDateTimeToMap(time);
        this.granularity = granularity;
    }

    public FragmentationTimestamp(Map<String, String> time) {
        this.timeMap = time;
        this.granularity = TimeBasedConstants.temporalFields[time.size()-1];
    }

    public String getGranularity() {
        return granularity;
    }

    public int getGranularityNumber() {
        return ArrayUtils.indexOf(temporalFields, granularity);
    }

    public Map<String, String> getTemporalFieldsWithValues() {
        Map<String, String> map = new HashMap<>();
        for(int i = 0; i <= getGranularityNumber(); i++) {
            map.put(temporalFields[i], timeMap.get(temporalFields[i]));
        }
        return map;
    }

    public String asString() {


        return "";
    }

    private Map<String, String> localDateTimeToMap(LocalDateTime time) {
        Map<String, String> map = new HashMap<>();
        map.put(YEAR, String.valueOf(time.getYear()));
        map.put(MONTH, String.valueOf(time.getMonthValue()));
        map.put(DAY, String.valueOf(time.getDayOfMonth()));
        map.put(HOUR, String.valueOf(time.getHour()));
        map.put(MINUTE, String.valueOf(time.getMinute()));
        map.put(SECOND, String.valueOf(time.getSecond()));
        return map;
    }
}
