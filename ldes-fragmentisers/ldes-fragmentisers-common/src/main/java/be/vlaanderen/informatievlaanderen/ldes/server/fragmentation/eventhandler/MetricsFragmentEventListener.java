package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.FragmentDeletedEvent;
import io.micrometer.core.instrument.Metrics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MetricsFragmentEventListener {

    private static final String LDES_SERVER_DELETED_FRAGMENTS_COUNT = "ldes_server_deleted_fragments_count";
    @EventListener
    public void handleFragmentDeletedEvent(FragmentDeletedEvent event) {
        String viewName = event.ldesFragmentIdentifier().getViewName().asString();
        Metrics.counter(LDES_SERVER_DELETED_FRAGMENTS_COUNT, "view",  viewName, "fragmentation-strategy", "pagination").increment();
    }
}
