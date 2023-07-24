package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentationTimestamp {

    private LocalDateTime time;

    public Map<String, String> getTemporalFieldsWithValues() {
        Map<String, String> map = new HashMap<>();
        map.put("Y", String.valueOf(time.getYear()));
        map.put("M", String.valueOf(time.getMonthValue()));
        map.put("D", String.valueOf(time.getDayOfMonth()));
        map.put("h", String.valueOf(time.getHour()));
        map.put("m", String.valueOf(time.getMinute()));
        map.put("s", String.valueOf(time.getSecond()));
    }
}
