package be.vlaanderen.informatievlaanderen.ldes.server.domain.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;

import java.util.HashMap;
import java.util.Map;

public class GaugeBuilder {

    static Map<String, Count> countersList = new HashMap<>();

    static Count buildGauge(String name, double d) {

        Count counter = new Count(name, d);
        countersList.put(name, counter);

        return counter;
    }

    public static Count getGauge(String name) {
        return (countersList.containsKey(name)) ? countersList.get(name) : buildGauge(name);
    }

    public static class Count {
        private double d;
        private Gauge gauge;
        public Count(String name, double d) {
            this.d = d;
            Gauge.builder(name, this::getCount).register(Metrics.globalRegistry);
        }
        public void inc(){
            d += 1;
        }
        public void dec(){
            d -= 1;
        }
        public void inc(long i){
            d += i;
        }
        public void dec(long i){
            d -= i;
        }
        double getCount() {
            return d;
        }
    }
}
