package be.vlaanderen.informatievlaanderen.ldes.server.domain.metrics;

import io.prometheus.client.Gauge;

import java.util.HashMap;
import java.util.Map;

public class GaugeBuilder {

    static Map<String, Gauge> countersList = new HashMap<>();

    static Gauge buildGauge(String name) {

        Gauge gauge = Gauge.build()
                .name(name)
                .labelNames(name)
                .help("123")
                .register();

        countersList.put(name, gauge);

        return gauge;
    }

    public static Gauge getGauge(String name) {
        return (countersList.containsKey(name)) ? countersList.get(name) : buildGauge(name);
    }
}
