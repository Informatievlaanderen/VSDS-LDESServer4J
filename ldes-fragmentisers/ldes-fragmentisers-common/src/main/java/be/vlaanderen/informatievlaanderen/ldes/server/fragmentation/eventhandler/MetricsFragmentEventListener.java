package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.BulkFragmentDeletedEvent;
import io.micrometer.core.instrument.Metrics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MetricsFragmentEventListener {

	private static final String LDES_SERVER_DELETED_FRAGMENTS_COUNT = "ldes_server_deleted_fragments_count";
	private static final String VIEW = "view";
	private static final String FRAGMENTATION_STRATEGY = "fragmentation-strategy";

	@EventListener
	public void handleBulkFragmentDeletedEvent(BulkFragmentDeletedEvent event) {
		event.ldesFragmentIdentifiers()
				.stream()
				.collect(Collectors.groupingBy(id -> id.getViewName().asString(), Collectors.counting()))
				.forEach((view, count) ->
						Metrics.counter(LDES_SERVER_DELETED_FRAGMENTS_COUNT, VIEW, view, FRAGMENTATION_STRATEGY, "pagination")
								.increment(count));
	}
}
